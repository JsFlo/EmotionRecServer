package com.emotionrec.tfinference.exts

import org.tensorflow.Tensor

fun createTensor(obj: Any): Tensor<*> = when (obj) {
    is String -> obj.createStringTensor()
    else -> Tensor.create(obj, obj::class.java)
}


// assuming all utf-8
fun String.createStringTensor(): Tensor<*> = Tensor.create(this.toByteArray(charset("UTF-8"))) as Tensor<String>

