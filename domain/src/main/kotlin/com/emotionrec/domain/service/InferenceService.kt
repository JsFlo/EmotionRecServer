package com.emotionrec.api.service

import com.emotionrec.api.models.InferenceInput
import com.emotionrec.api.models.PredictionGroup

// gcp or custom
interface InferenceService {
    fun getPrediction(inferenceInput: InferenceInput): PredictionGroup
}
