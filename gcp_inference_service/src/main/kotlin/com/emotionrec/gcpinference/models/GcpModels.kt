package com.emotionrec.gcpinference.models

import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.Prediction
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.models.toEmotion

// INPUT
class GcpPredictionInput(val instances: Array<GcpPredictionInstance>) {
    companion object {
        fun create(inferenceInputs: List<InferenceInput>): GcpPredictionInput {
            val predictionInstances = inferenceInputs.map { it.toGcpPredictionInstance() }
            return GcpPredictionInput(predictionInstances.toTypedArray())
        }
    }
}

fun List<InferenceInput>.toGcpPredictionInput(): GcpPredictionInput {
    return GcpPredictionInput.create(this)
}

class GcpPredictionInstance(val images: List<List<Array<Float>>>) {
    companion object {
        fun create(inferenceInput: InferenceInput): GcpPredictionInstance {
            return GcpPredictionInstance(inferenceInput.images.map { it.map { it.arrOfRGB } })
        }
    }
}

fun InferenceInput.toGcpPredictionInstance(): GcpPredictionInstance {
    return GcpPredictionInstance.create(this)
}

// OUTPUT
class GcpPredictionResult(val predictions: Array<GcpPredictionScores>) {
    fun toPredictionGroups(): List<PredictionGroup> {
        return predictions.map { it.toPredictionGroup() }
    }
}

class GcpPredictionScores(val scores: Array<Float>) {

    fun toPredictionGroup(): PredictionGroup {
        return PredictionGroup(softmax(scores)
                .mapIndexed { index, prob -> Pair(index, prob) }
                .map { it.toPrediction() })

    }

    private fun softmax(scores: Array<Float>): List<Double> {
        val expScores = scores.map { Math.exp(it.toDouble()) }
        val sum = expScores.sum()
        return expScores.map { it / sum }
    }
}

private fun Pair<Int, Double>.toPrediction(): Prediction {
    return Prediction(second, first.toEmotion())
}
