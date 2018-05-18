package com.emotionrec.tfinference.models

import com.emotionrec.domain.models.InferenceInput

// input transformations

typealias OutsideArray<T> = Array<T>
typealias Column<T> = Array<T>
typealias Row<T> = Array<T>
typealias RGB<T> = Array<T>

fun List<InferenceInput>.toLocalInferenceInput(): OutsideArray<Column<Row<RGB<Float>>>> {
    return this.map {
        it.toLocalInferenceInput()
    }.toTypedArray()
}

fun InferenceInput.toLocalInferenceInput(): Column<Row<RGB<Float>>> {
    return this.images.map {
        it.map {
            arrayOf(it.r, it.b, it.g)
        }.toTypedArray()
    }.toTypedArray()
}
