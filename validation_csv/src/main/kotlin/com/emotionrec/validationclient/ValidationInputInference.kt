package com.emotionrec.validationclient

import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.ErrorRate
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.gcpinference.GcpInferenceService
import com.emotionrec.validationclient.datainput.getFormattedInput

fun main(args: Array<String>) {
    val inferenceService = GcpInferenceService()
    var formattedInputData = getFormattedInput(100)
    formattedInputData = formattedInputData.shuffled().take(20)

    inferenceService.getPrediction(formattedInputData.toInferenceInput())
            .fold(
                    { println("Failed: $it") },
                    {
                        it.forEachIndexed { index, predictionGroup ->
                            println(predictionGroup)
                            val correctEmotion = formattedInputData[index].second
                            println("""
                        |   Correct Emotion: $correctEmotion
                        |   Error Rate: ${ErrorRate.getErrorRate(predictionGroup, correctEmotion)}
                        """.trimMargin())
                        }
                    })
}


fun List<Pair<InferenceInput, Emotion>>.toInferenceInput(): List<InferenceInput> {
    return this.map { it.first }
}

