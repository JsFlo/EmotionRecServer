package com.emotionrec.gcpinference.validationinput

import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.ErrorRate
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.gcpinference.GcpInferenceService
import org.junit.Assert
import org.junit.Test

class ValidationInputTest {
    @Test
    fun getPrediction_shuffled20() {
        val inferenceService = GcpInferenceService()
        var formattedInputData = getFormattedInput(100)
        formattedInputData = formattedInputData.shuffled().take(20)

        inferenceService.getPrediction(formattedInputData.toInferenceInput())
                .fold(
                        {
                            Assert.fail("Inference failed: $it")
                            println("Failed: $it")
                        },
                        {
                            it.forEachIndexed { index, predictionGroup ->
                                println(predictionGroup)
                                val correctEmotion = formattedInputData[index].second
                                println("""
                        |   Correct Emotion: $correctEmotion
                        |   Error Rate: ${ErrorRate.getErrorRate(predictionGroup, correctEmotion)}
                        """.trimMargin())
                            }

                            Assert.assertTrue(it.size == 20)
                        })
    }


    fun List<Pair<InferenceInput, Emotion>>.toInferenceInput(): List<InferenceInput> {
        return this.map { it.first }
    }
}
