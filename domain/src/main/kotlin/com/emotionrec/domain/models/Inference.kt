package com.emotionrec.domain.models

data class Shape(val columns: Int, val rows: Int)

val INPUT_SHAPE = Shape(48, 48)

data class InferenceInput(val images: List<List<RGB>>, private val shape: Shape = INPUT_SHAPE) {
    init {
        require(images.size == shape.rows)
        require(images.all { it.size == shape.columns })
    }
}

class RGB(arrOfRGB: Array<Float>) {
    init {
        require(arrOfRGB.size == 3)
    }
}