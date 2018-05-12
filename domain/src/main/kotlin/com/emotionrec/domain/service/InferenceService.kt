package com.emotionrec.domain.service

import arrow.core.Try
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup

// gcp or custom
interface InferenceService {
    // TODO: change to sync
    fun getPrediction(inferenceInputs: List<InferenceInput>): Try<List<PredictionGroup>>
}

// validation input or api
abstract class InferenceServerClient(inferenceService: InferenceService)
