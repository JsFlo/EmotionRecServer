package com.emotionrec.tfinference.exts

inline fun <reified T> multiArray(array1: T, array2: T): Array<T> {
    return arrayOf(array1, array2)
}

inline fun <reified T> multiArrayOfValue(size: Int, value: T): Array<T> {
    val list = mutableListOf<T>()
    for (i in 0 until size) {
        list.add(value)
    }
    return list.toTypedArray()
}

fun multiArrayOfZeros(size: Int): Array<Float> {
    return multiArrayOfValue(size, 0f)
}