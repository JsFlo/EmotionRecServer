package com.emotionrec.domain.models

class PredictionGroup(predictions: List<Prediction>) {
    val sortedPredictions = predictions.sortedByDescending { it.probability }

    init {
        require(sortedPredictions.size == Emotion.validEmotionSize)
    }

}

data class Prediction(val probability: Probability, val emotion: Emotion)

typealias Probability = Double