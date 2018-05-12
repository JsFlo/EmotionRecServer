package com.emotionrec.validationclient

import arrow.core.recover
import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.ErrorRate
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.service.InferenceServerClient
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.validationclient.datainput.getFormattedInput

class ValidationInputInference(inferenceService: InferenceService) : InferenceServerClient(inferenceService) {

    init {
        var formattedInputData = getFormattedInput(100)
        formattedInputData = formattedInputData.shuffled().take(20)

//        inferenceService.getPrediction(formattedInputData.toInferenceInput(), {
//            it.map {
//                it.forEachIndexed { index, predictionGroup ->
//                    println(predictionGroup)
//                    val correctEmotion = formattedInputData[index].second
//                    println("""
//                        |   Correct Emotion: $correctEmotion
//                        |   Error Rate: ${ErrorRate.getErrorRate(predictionGroup, correctEmotion)}
//                        """.trimMargin())
//                }
//            }
//                    .recover { println("Failed: $it") }
//        })
    }

    fun List<Pair<InferenceInput, Emotion>>.toInferenceInput(): List<InferenceInput> {
        return this.map { it.first }
    }
}
