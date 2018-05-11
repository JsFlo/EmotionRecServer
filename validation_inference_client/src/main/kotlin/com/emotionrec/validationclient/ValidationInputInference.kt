package com.emotionrec.validationclient

import arrow.core.recover
import com.emotionrec.domain.service.InferenceServerClient
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.validationclient.datainput.getFormattedInput
import com.emotionrec.validationclient.models.toInferenceInput

class ValidationInputInference(inferenceService: InferenceService) : InferenceServerClient(inferenceService) {

    init {
        var formattedInputData = getFormattedInput(100)
        formattedInputData = formattedInputData.shuffled().take(20)

        val predictionGroups = inferenceService.getPrediction(formattedInputData.toInferenceInput())
        predictionGroups
                .map { it.forEach { println(it) } }
                .recover { println("Failed: $it") }
    }
}