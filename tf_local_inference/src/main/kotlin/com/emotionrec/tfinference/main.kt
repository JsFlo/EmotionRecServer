package com.emotionrec.tfinference

import com.emotionrec.domain.utils.ValidationInputRetrieval
import com.emotionrec.tfinference.exts.runFirstTensor
import org.tensorflow.SavedModelBundle
import org.tensorflow.Tensor

typealias OutsideArray<T> = Array<T>
typealias Column<T> = Array<T>
typealias Row<T> = Array<T>
typealias RGB<T> = Array<T>

fun Tensor<*>.getFloatArrayOutput(): Array<out FloatArray> {
    return JavaUtils.getFloatArrayOutput(this, 1, 7)
}

fun ValidationInputRetrieval.getInput(inputRet: ValidationInputRetrieval, numberOfInputs: Int): OutsideArray<Column<Row<RGB<Float>>>> {
    val (inferenceInput, correctEmotion) = inputRet.getFormattedInput(1)[0]

    // [1, 48, 48, 3]
    val input: OutsideArray<Column<Row<RGB<Float>>>> =
            arrayOf(
                    inferenceInput.images.map {
                        // column 48
                        it.map {
                            // row 48
                            arrayOf(it.r, it.b, it.g)//rgb 3
                        }.toTypedArray()
                    }.toTypedArray()
            )
}

fun main(args: Array<String>) {
    val load = SavedModelBundle.load("./1", "serve")

    val inputRet = ValidationInputRetrieval()

    val (inferenceInput, correctEmotion) = inputRet.getFormattedInput(1)[0]

    // [1, 48, 48, 3]
    val input: OutsideArray<Column<Row<RGB<Float>>>> =
            arrayOf(
                    inferenceInput.images.map {
                        // column 48
                        it.map {
                            // row 48
                            arrayOf(it.r, it.b, it.g)//rgb 3
                        }.toTypedArray()
                    }.toTypedArray()
            )

    val tensorInput = Tensor.create(input)
    load.session().runner()
            .feed("input_2", tensorInput)
//            .fetch("sequential_1/dense_4/Softmax")
            .fetch("sequential_1/dense_4/BiasAdd")
            .runFirstTensor {
                println("Output Shape: ${it.shape().joinToString()}")
                val result: Array<out FloatArray> = it.getFloatArrayOutput()
                val arrPredictions = result[0]
                println("Result: ${arrPredictions.joinToString()}")
            }
}