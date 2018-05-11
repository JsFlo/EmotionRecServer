package com.emotionrec.api.models

enum class Emotion {
    ANGRY,
    DISGUST,
    FEAR,
    HAPPY,
    SAD,
    SURPRISE,
    NEUTRAL,
    VULCAN;

    companion object {
        val emotionValues = Emotion.values()
        val validEmotionSize = emotionValues.size - 1
    }
}