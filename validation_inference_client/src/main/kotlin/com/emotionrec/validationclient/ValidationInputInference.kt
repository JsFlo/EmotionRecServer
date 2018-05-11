package com.emotionrec.validationclient

import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.service.InferenceServerClient
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.validationclient.models.toInferenceInput

class ValidationInputInference(inferenceService: InferenceService) : InferenceServerClient(inferenceService) {

    init {
        var formattedInputData = getFormattedInput(100)
        formattedInputData = formattedInputData.shuffled().take(20)

        val predictionGroup: PredictionGroup = inferenceService.getPrediction(formattedInputData.toInferenceInput())


    }
}