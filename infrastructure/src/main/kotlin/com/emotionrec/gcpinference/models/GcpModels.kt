package com.emotionrec.gcpinference.models

import com.emotionrec.domain.models.*

// INPUT
class GcpPredictionInput(val instances: Array<GcpPredictionInstance>)

class GcpPredictionInstance(val images: List<List<Array<Float>>>)

fun List<InferenceInput>.toGcpPredictionInput(): GcpPredictionInput {
    val predictionInstances = this.map { it.toGcpPredictionInstance() }
    return GcpPredictionInput(predictionInstances.toTypedArray())
}

fun InferenceInput.toGcpPredictionInstance(): GcpPredictionInstance {
    return GcpPredictionInstance(this.images.map { it.map { arrayOf(it.r, it.g, it.b) } })
}

// RESULT
class GcpPredictionResult(val predictions: Array<GcpPredictionScores>) {
    fun toPredictionGroups(): List<PredictionGroup> {
        return predictions.map { it.scores.toPredictionGroup(false) }
    }
}

data class GcpPredictionScores(val scores: Array<Float>)
