package com.emotionrec.domain.service

import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup

// gcp or custom
interface InferenceService {
    fun getPrediction(inferenceInput: List<InferenceInput>): PredictionGroup
}

// validation input or api
abstract class InferenceServerClient(inferenceService: InferenceService)
