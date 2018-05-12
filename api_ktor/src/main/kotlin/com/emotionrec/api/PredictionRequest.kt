package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.api.models.PredictionResult
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.models.RGB
import com.emotionrec.domain.service.InferenceService


sealed class PredictionError(val message: String) {
    class MissingInput(message: String) : PredictionError(message)
    class InvalidInput(message: String) : PredictionError(message)
    class TodoErr : PredictionError("Err")
}


fun predictionInput(imageArray: String?, delimeter: String = " ", inferenceService: InferenceService)
        : Either<PredictionError, PredictionResult> {
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

fun getInferenceInput(imageArrayString: String, delimeter: String): Either<PredictionError, InferenceInput> {
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

fun getPrediction(inferenceService: InferenceService, inferenceInput: InferenceInput): Either<PredictionError, List<PredictionGroup>> {
    return inferenceService.getPrediction(listOf(inferenceInput))
            .fold({ Either.left(PredictionError.TodoErr()) },
                    { Either.right(it) })
}

fun List<PredictionGroup>.toPredictionResult(): PredictionResult {
    return PredictionResult(this[0].sortedPredictions, this[0].sortedPredictions[0])
}