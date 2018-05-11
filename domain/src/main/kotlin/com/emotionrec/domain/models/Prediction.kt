package com.emotionrec.domain.models

import java.text.DecimalFormat

class PredictionGroup(predictions: List<Prediction>) {
    val sortedPredictions = predictions.sortedByDescending { it.probability }

    init {
        require(sortedPredictions.size == Emotion.validEmotionSize)
    }

    override fun toString(): String {
        return """
                |---------------------------------
                |PREDICTION SCORES
                |
                |${sortedPredictions.joinToString(separator = "\n")}
                |
                |Guessed Emotion: ${sortedPredictions.first().emotion}
                |
                |---------------------------------
            """.trimMargin()
    }

}

data class Prediction(val probability: Probability, val emotion: Emotion) {
    companion object {
        private val decimalFormat = DecimalFormat("##")
    }

    override fun toString(): String {
        return "    ${decimalFormat.format(probability * 100)}% $emotion"
    }
}

typealias Probability = Double