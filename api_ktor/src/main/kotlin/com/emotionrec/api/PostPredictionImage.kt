package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.api.responses.PredictionResponse
import com.emotionrec.api.responses.toPredictionResult
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.RGB
import com.emotionrec.domain.service.InferenceService
import io.ktor.application.call
import io.ktor.content.PartData
import io.ktor.content.forEachPart
import io.ktor.network.util.ioCoroutineDispatcher
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.yield
import mu.KotlinLogging
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.imageio.ImageIO


private val logger = KotlinLogging.logger { }
/**
 * Accepts mutlipart file image upload
 */
fun Routing.postPredictionImage(inferenceService: InferenceService) {
    post("/predictionImage") {
        logger.debug { "/predictionImage" }
        val multipart = call.receiveMultipart()
        var name = ""
        var imageInputStream: InputStream? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> if (part.name == "name") {
                    name = part.value
                    logger.debug { "Name: $name" }
                }
                is PartData.FileItem -> {
                    imageInputStream = part.streamProvider()
                }
            }

            part.dispose()
        }


        if (imageInputStream != null) {
            // create a file where we will put the image file being uploaded
            val file = File("upload-${System.currentTimeMillis()}-$name")

            // copy image input stream into the file created
            imageInputStream.use { its -> file.outputStream().buffered().use { its!!.copyToSuspend(it) } }

            // predict
            val result = getImagePrediction(file, inferenceService)

            // delete the file
            file.delete()

            // respond
            when (result) {
                is Either.Left -> call.respond(result.a)
                is Either.Right -> call.respond(result.b)
            }
        } else {
            call.respond(PredictionError.MissingInput("Error with file upload."))
        }


    }
}

/**
 * Converts the [file] image passed in into an [InferenceInput] and returns the [PredictionResponse] or a [PredictionError].
 */
private fun getImagePrediction(file: File, inferenceService: InferenceService): Either<PredictionError, PredictionResponse> {
    val resizedImageFile = resizeImageFile(file)
    val inferenceInput = convertToGrayScale(resizedImageFile)
    val result = inferenceService.getPrediction(listOf(inferenceInput))
    result.fold(
            { return Either.left(PredictionError.TodoErr()) },
            { return Either.right(it.toPredictionResult()) }
    )
}

private fun convertToGrayScale(file: File): InferenceInput {
    val img = ImageIO.read(file)

    //get image width and height
    val width = img.width
    val height = img.height

    //convert to grayscale
    val rowRgbList = mutableListOf<List<RGB>>()
    for (y in 0 until height) {
        val rowRgb = mutableListOf<RGB>()
        for (x in 0 until width) {
            val p = img.getRGB(x, y)

//            val a = p shr 24 and 0xff
            val r = p shr 16 and 0xff
            val g = p shr 8 and 0xff
            val b = p and 0xff

            //calculate average
            val avg = (r + g + b) / 3

            val pixelValue = avg / 255.0f
            val rgb = RGB(pixelValue, pixelValue, pixelValue)// rgb with same values
            rowRgb.add(rgb)
        }
        rowRgbList.add(rowRgb)
    }
    return InferenceInput(rowRgbList)
}

/**
 * Resizes image file [file] to a specific width [scaledWidth] and height [scaledHeight].
 */
private fun resizeImageFile(file: File, scaledWidth: Int = 48, scaledHeight: Int = 48): File {
    val inputImage = ImageIO.read(file)

    // creates output image
    val outputImage = BufferedImage(scaledWidth,
            scaledHeight, inputImage.type)

    // scales the input image to the output image
    val g2d = outputImage.createGraphics()
    g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null)
    g2d.dispose()


    ImageIO.write(outputImage, "jpg", file)
    return file
}

/**
 * Copies the input stream (Receiver) to an [OutputStream]
 */
private suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 4 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = ioCoroutineDispatcher
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}