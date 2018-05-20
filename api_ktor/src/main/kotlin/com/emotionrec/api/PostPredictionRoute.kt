package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.api.responses.PredictionResponse
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.models.RGB
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.gcpinference.GcpInferenceService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

sealed class PredictionError(val message: String) {
    class MissingInput(message: String) : PredictionError(message)
    class InvalidInput(message: String) : PredictionError(message)
    class TodoErr : PredictionError("Err")
}

data class PostPredictionData(val image_array: String, val delimeter: String?)

fun Routing.postPrediction(inferenceService: InferenceService) {
    post("/prediction") {
        logger.debug { "/prediction called" }
        val postData = call.receive<PostPredictionData>()
        val result = predictionInput(postData.image_array, postData.delimeter ?: " ", inferenceService)
        when (result) {
            is Either.Right -> call.respond(result.b)
            is Either.Left -> call.respond(result.a)
        }
    }
}

private fun predictionInput(imageArray: String?, delimeter: String = " ", inferenceService: InferenceService)
        : Either<PredictionError, PredictionResponse> {
    return if (imageArray.isNullOrEmpty()) {
        Either.left(PredictionError.MissingInput("Image array empty"))
    } else {
        val inferenceInput = getInferenceInput(imageArray!!, delimeter)
        when (inferenceInput) {
            is Either.Left -> Either.left(inferenceInput.a)
            is Either.Right -> getPrediction(inferenceService, inferenceInput.b).map { it.toPredictionResult() }
        }
    }
}

private fun getInferenceInput(imageArrayString: String, delimeter: String): Either<PredictionError, InferenceInput> {
    val pixelFloatArray = imageArrayString.split(delimeter)
            .map { it.toFloatOrNull() }
            .filter { it != null } as List<Float>

    return if (pixelFloatArray.size != 2304) {
        Either.left(PredictionError.InvalidInput("Input should be 2304 floats separated by:$delimeter"))
    } else {
        val rowRgbList = mutableListOf<List<RGB>>()
        for (i in 0 until 48) {
            val rowRgb = mutableListOf<RGB>()
            for (j in 0 until 48) {
                val pixelValue = pixelFloatArray[(i * 48) + j] / 255
                val rgb = RGB(pixelValue, pixelValue, pixelValue)// rgb with same values
                rowRgb.add(rgb)
            }
            rowRgbList.add(rowRgb)
        }
        Either.Right(InferenceInput(rowRgbList))
    }

}

private fun getPrediction(inferenceService: InferenceService, inferenceInput: InferenceInput): Either<PredictionError, List<PredictionGroup>> {
    return inferenceService.getPrediction(listOf(inferenceInput))
            .fold({ Either.left(PredictionError.TodoErr()) },
                    { Either.right(it) })
}

fun List<PredictionGroup>.toPredictionResult(): PredictionResponse {
    return PredictionResponse(this[0].sortedPredictions, this[0].sortedPredictions[0])
}