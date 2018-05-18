package com.emotionrec.gcpinference.models

import com.emotionrec.domain.models.*

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
            return GcpPredictionInstance(inferenceInput.images.map { it.map { arrayOf(it.r, it.g, it.b) } })
        }
    }
}

fun InferenceInput.toGcpPredictionInstance(): GcpPredictionInstance {
    return GcpPredictionInstance.create(this)
}

// RESULT
class GcpPredictionResult(val predictions: Array<GcpPredictionScores>) {
    fun toPredictionGroups(): List<PredictionGroup> {
        return predictions.map { it.scores.toPredictionGroup(true) }
    }
}

data class GcpPredictionScores(val scores: Array<Float>)
