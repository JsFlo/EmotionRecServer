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


private fun getImagePrediction(file: File): Either<PredictionError, PredictionResponse> {
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