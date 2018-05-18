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

data class Prediction(val probability: Float, val emotion: Emotion) {
    companion object {
        private val decimalFormat = DecimalFormat("##")
    }

    override fun toString(): String {
        return "    ${decimalFormat.format(probability * 100)}% $emotion"
    }
}

fun Array<Float>.toPredictionGroup(softmax: Boolean = true): PredictionGroup {
    val values = if (softmax) {
        this.softmax()
    } else {
        this
    }

    return PredictionGroup(values.toListPrediction())
}

private fun Array<Float>.softmax(): Array<Float> {
    val expScores = this.map { Math.exp(it.toDouble()).toFloat() }
    val sum = expScores.sum()
    return expScores.map { it / sum }.toTypedArray()
}

private fun Array<Float>.toListPrediction(): List<Prediction> {
    return this.mapIndexed { index, prob -> Pair(index, prob) }
            .map { Prediction(it.second, it.first.toEmotion()) }
}
