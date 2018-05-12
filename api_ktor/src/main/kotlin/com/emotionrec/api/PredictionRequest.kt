package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.api.models.PredictionResult
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.service.InferenceService


sealed class PredictionError {
    object MissingInput : PredictionError()
    object InvalidInput : PredictionError()
    object Err : PredictionError()
}


fun predictionInput(imageArray: String?, delimeter: String = " ", inferenceService: InferenceService)
        : Either<PredictionError, List<PredictionGroup>> {
    return if (imageArray.isNullOrEmpty()) {
        Either.left(PredictionError.MissingInput)
    } else {
        val inferenceInput = getInferenceInput(imageArray!!, delimeter)
        inferenceInput.map {
            getPrediction(inferenceService, listOf(it))
        }

    }
}

fun getInferenceInput(imageArray: String, delimeter: String): Either<PredictionError, InferenceInput> {

}

fun getPrediction(inferenceService: InferenceService, inferenceInput: InferenceInput): Either<PredictionError, List<PredictionGroup>> {

}

fun List<PredictionGroup>.toPredictionResult(): PredictionResult {
    return PredictionResult(this[0].sortedPredictions, this[0].sortedPredictions[0])
}