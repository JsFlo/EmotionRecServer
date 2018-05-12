package com.emotionrec.api.models

data class PredictionResult(val angry: Int,
                       val disgust: Int,
                       val fear: Int,
                       val happy: Int,
                       val sad: Int,
                       val surprise: Int,
                       val neutral: Int,
                       val guessedEmotion: String,
                       val guessedEmotionIndex: Int) {
//    ANGRY,
//    DISGUST,
//    FEAR,
//    HAPPY,
//    SAD,
//    SURPRISE,
//    NEUTRAL,
}