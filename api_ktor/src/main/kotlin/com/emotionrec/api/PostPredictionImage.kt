package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.api.responses.PredictionResponse
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
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.Spring.height
import javax.swing.Spring.height
import java.awt.Graphics2D


private val logger = KotlinLogging.logger { }

fun Routing.postPredictionImage() {
    post("/predictionImage") {
        logger.debug { "/predictionImage" }
        val multipart = call.receiveMultipart()
        var name = ""
        var inputStream: InputStream? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> if (part.name == "name") {
                    name = part.value
                    logger.debug { "Name: $name" }
                }
                is PartData.FileItem -> {
                    inputStream = part.streamProvider()
                }
            }

            part.dispose()
        }


        if (inputStream != null) {
            val file = File("upload-${System.currentTimeMillis()}-$name")
            inputStream.use { its -> file.outputStream().buffered().use { its!!.copyToSuspend(it) } }
            val result = getImagePrediction(file)
            when (result) {
                is Either.Left -> call.respond(result.a)
                is Either.Right -> call.respond(result.b)
            }
        } else {
            call.respond(PredictionError.MissingInput("Error with file upload."))
        }


    }
}


private fun getImagePrediction(file: File, scaledWidth: Int = 48, scaledHeight: Int = 48): Either<PredictionError, PredictionResponse> {
//    val img = ImageIO.read(file)

    val inputImage = ImageIO.read(file)

    // creates output image
    val outputImage = BufferedImage(scaledWidth,
            scaledHeight, inputImage.type)

    // scales the input image to the output image
    val g2d = outputImage.createGraphics()
    g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null)
    g2d.dispose()

    // writes to output file
    ImageIO.write(outputImage, "jpg", File ("ahhuuuu.jpg"))

//    //get image width and height
//    val width = img.width
//    val height = img.height
//
//    //convert to grayscale
//    for (y in 0 until height) {
//        for (x in 0 until width) {
//            var p = img.getRGB(x, y)
//
//            val a = p shr 24 and 0xff
//            val r = p shr 16 and 0xff
//            val g = p shr 8 and 0xff
//            val b = p and 0xff
//
//            //calculate average
//            val avg = (r + g + b) / 3
//
//            //replace RGB value with avg
//            p = a shl 24 or (avg shl 16) or (avg shl 8) or avg
//
//            img.setRGB(x, y, p)
//        }
//    }

    return Either.left(PredictionError.TodoErr())
}

suspend fun InputStream.copyToSuspend(
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