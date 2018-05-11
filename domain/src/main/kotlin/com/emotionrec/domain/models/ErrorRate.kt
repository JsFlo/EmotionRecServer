package com.emotionrec.domain.models

enum class ErrorRate {
    TOP_1,
    TOP_2,
    TOP_3,
    TOP_4,
    TOP_5,
    TOP_6,
    TOP_7,
    NONE;

    companion object {
        val errorRateValues = ErrorRate.values()

        fun getErrorRate(predictionGroup: PredictionGroup, correctEmotion: Emotion): ErrorRate {
            val correctIndexPrediction = predictionGroup.sortedPredictions
                    .mapIndexed { index, prediction -> Pair(index, prediction) }
                    .find { it.second.emotion == correctEmotion }

            fun Int?.toErrorRate(): ErrorRate {
                return if (this != null && this in 0..6)
                    ErrorRate.errorRateValues[this]
                else
                    ErrorRate.NONE
            }

            return correctIndexPrediction?.first.toErrorRate()
        }
    }
}
