package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.api.PredictionError.*
import com.emotionrec.api.responses.PredictionResponse
import com.emotionrec.api.responses.toPredictionResult
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.RGB
import com.emotionrec.domain.service.InferenceService
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

const val DEFAULT_DELIMITER = " "
const val EXPECTED_ARRAY_SIZE = 2304

data class PostPredictionData(val image_array: String, val delimiter: String?)

/**
 * Accepts [PostPredictionData].
 *
 * Expects the [PostPredictionData.image_array]:
 *   * to be an array of size [EXPECTED_ARRAY_SIZE]
 *   * String array separated by a delimiter [PostPredictionData.delimiter] (default: [DEFAULT_DELIMITER])
 *
 *
 * Responds with [PredictionError] or [PredictionResponse]
 */
fun Routing.postPredictionImageArray(inferenceService: InferenceService) {
    post("/prediction") {
        logger.debug { "/prediction called" }

        // convert to PostPredictionData
        val postData = call.receive<PostPredictionData>()

        // get prediction
        val result = getPrediction(postData.image_array, postData.delimiter ?: DEFAULT_DELIMITER, inferenceService)

        // respond
        when (result) {
            is Either.Right -> call.respond(result.b)
            is Either.Left -> call.respond(result.a)
        }
    }
}

/**
 * Validates [imageArray] input, converts [imageArray] to [InferenceInput] and returns the prediction
 * from the [inferenceService].
 *
 * @return [Either<PredictionError, PredictionResponse>]
 */
private fun getPrediction(imageArray: String?, delimiter: String, inferenceService: InferenceService)
        : Either<PredictionError, PredictionResponse> {

    // check if empty
    return if (imageArray.isNullOrEmpty()) {
        Either.left(MissingInput("Image array empty"))
    } else {

        // convert to float
        val pixelFloatArray: List<Float> = imageArray!!.toPixelFloatList(delimiter)

        // check size
        if (pixelFloatArray.size != EXPECTED_ARRAY_SIZE) {
            Either.left(InvalidInput("Input should be 2304 floats separated by:$delimiter"))
        } else {

            val inferenceInput = pixelFloatArray.toInferenceInput()

            // return Either<err, prediction>
            inferenceService.getPrediction(inferenceInput)
        }

    }
}

/**
 * Wraps result of [InferenceService.getPrediction] into a [Either<PredictionError, PredictionResponse>].
 */
private fun InferenceService.getPrediction(inferenceInput: InferenceInput): Either<TodoErr, PredictionResponse> {
    return this.getPrediction(listOf(inferenceInput))
            .fold({ Either.left(TodoErr()) }, { Either.right(it) })
            .map { it.toPredictionResult() }
}

/**
 * Converts a [String] of an int array into a [List] of [Float]s.
 */
private fun String.toPixelFloatList(delimiter: String): List<Float> {
    return this.split(delimiter)
            .map { it.toFloatOrNull() }
            .filterNotNull()

}

private fun List<Float>.toInferenceInput(): InferenceInput {
    val rowRgbList = mutableListOf<List<RGB>>()
    for (i in 0 until 48) {
        val rowRgb = mutableListOf<RGB>()
        for (j in 0 until 48) {
            val pixelValue = this[(i * 48) + j] / 255
            val rgb = RGB(pixelValue, pixelValue, pixelValue)// rgb with same values
            rowRgb.add(rgb)
        }
        rowRgbList.add(rowRgb)
    }
    return InferenceInput(rowRgbList)
}