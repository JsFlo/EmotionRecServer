package com.emotionrec.tfinference.exts

import org.tensorflow.Session
import org.tensorflow.Tensor

fun Session.Runner.runFirstTensor(funToRun: (t: Tensor<*>) -> Unit) {
    run()[0].use { output ->
        funToRun(output)
    }
}