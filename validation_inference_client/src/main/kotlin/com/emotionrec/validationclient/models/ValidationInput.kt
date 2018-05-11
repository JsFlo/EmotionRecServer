package com.emotionrec.validationclient.models

import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.RGB

data class RowData(val emotion: String, val pixels: String)

// pixels = 48(List) x 48(List) x 3(Array)
data class EmotionPixelsData(val emotion: Emotion, val pixels: List<List<Array<Float>>>) {
    fun toInferenceInput(): InferenceInput {
        return InferenceInput(pixels.map { it.map { RGB(it) } })
    }
}

fun List<EmotionPixelsData>.toInferenceInput(): List<InferenceInput> {
    return this.map { it.toInferenceInput() }
}