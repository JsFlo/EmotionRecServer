package com.emotionrec.gcpinference.validationinput

import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.ErrorRate
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.utils.ValidationInputRetrieval
import com.emotionrec.domain.utils.printInput
import com.emotionrec.gcpinference.GcpInferenceService
import org.junit.Assert
import org.junit.Test

class ValidationInputTest {
    @Test
    fun getPrediction_shuffled20() {
        val inferenceService = GcpInferenceService()
        val validationInputRetrieval = ValidationInputRetrieval()
        var formattedInputData = validationInputRetrieval.getFormattedInput(20)
//        formattedInputData = formattedInputData.shuffled().take(1)
        printInput(formattedInputData.toInferenceInput()[0].images)
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

                            Assert.assertTrue(it.size == formattedInputData.size)
                        })
    }


    fun List<Pair<InferenceInput, Emotion>>.toInferenceInput(): List<InferenceInput> {
        return this.map { it.first }
    }
}
