package com.emotionrec.tfinference

import com.emotionrec.domain.utils.ValidationInputRetrieval
import org.tensorflow.SavedModelBundle
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path


typealias OutsideArray<T> = Array<T>
typealias Column<T> = Array<T>
typealias Row<T> = Array<T>
typealias RGB<T> = Array<T>

//[?, 48, 48, 3]
fun ValidationInputRetrieval.getInput(numberOfInputs: Int): OutsideArray<Column<Row<RGB<Float>>>> {
    val formattedInput = this.getFormattedInput(numberOfInputs).map { it.first }
    // [1, 48, 48, 3]
    val input: OutsideArray<Column<Row<RGB<Float>>>> =
            formattedInput.map {
                it.images.map {
                    it.map {
                        arrayOf(it.r, it.b, it.g)//rgb 3
                    }.toTypedArray()
                }.toTypedArray()

            }.toTypedArray()
//    printInput(input[0])

    return input
}

fun main(args: Array<String>) {
    val load: SavedModelBundle = SavedModelBundle.load("./fifSavedModel", "serve")

    load.session().runner()
    val inputRet = ValidationInputRetrieval()
    val result: Array<out FloatArray> = JavaUtils.runInference(load.session(), inputRet.getInput(1))
    result.forEach { println(it.joinToString()) }
}