package com.emotionrec.domain.models

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

fun Int?.toEmotion(): Emotion {
    return if (this != null && this in 0..6)
        Emotion.emotionValues[this]
    else
        Emotion.VULCAN
}