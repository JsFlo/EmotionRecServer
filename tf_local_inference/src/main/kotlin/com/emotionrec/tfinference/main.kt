package com.emotionrec.tfinference

import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.utils.ValidationInputRetrieval
import com.emotionrec.domain.utils.printInput
import com.emotionrec.tfinference.exts.runFirstTensor
import org.tensorflow.SavedModelBundle
import org.tensorflow.Session
import org.tensorflow.Tensor

typealias OutsideArray<T> = Array<T>
typealias Column<T> = Array<T>
typealias Row<T> = Array<T>
typealias RGB<T> = Array<T>

fun Tensor<*>.getFloatArrayOutput(numberOfInputs: Int = 1): Array<out FloatArray> {
    return JavaUtils.getFloatArrayOutput(this, numberOfInputs, 7)
}

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
    val x: Array<Array<Array<Float>>> = input[0]
    printInput(input[0])
    return input
}

fun main(args: Array<String>) {
    val load: SavedModelBundle = SavedModelBundle.load("./no_gpu_model", "serve")

    load.session().runner()
    val inputRet = ValidationInputRetrieval()
    val numberOfInputs = 3
    val tensorInput = Tensor.create(inputRet.getInput(numberOfInputs))
    println("tensor shape $tensorInput")
    val graph = load.graph()
    val initOp = graph.operation("init")
    val x = graph.operations().asSequence().map { it }.filter { it.type() ==  initOp.type()}.forEach { println("AYYY: $it") }
//    println(x.toList().size)
//    Session.Run
//    graph.operations().forEach { println("${it.name()}") }
//    val x = graph.operations().asSequence().map { it }
//    val y = x.groupBy { it.type() }
//    y.forEach { t, u -> println("$t, size: ${u.size}") }
//    println("DFKDS" + graph.operation("init").type())

//    load.session().runner()
//    val session = Session(graph)
    val runner = load.session().runner()
//    runner.addTarget("init").run()
//    runner.fetch("init:0").run()
//    runner
//            .addTarget("init")
//            .runAndFetchMetadata()
    runner
            .addTarget("init")
            .addTarget("init_1")
            .addTarget("init_2")
//            .addTarget("group_deps")
//            .addTarget("group_deps_1")
//            .addTarget("group_deps_2")
            .feed("input_2", tensorInput)
//            .feed("input_1", tensorInput)
            .fetch("sequential_1/dense_4/Softmax")
//            .fetch("init:0")
//            .fetch("sequential_1/dense_4/BiasAdd")
            .runFirstTensor {
                println("Output Shape: ${it.shape().joinToString()}")
                val result: Array<out FloatArray> = it.getFloatArrayOutput(numberOfInputs)
                result.forEach { println("Result: ${it.joinToString()}") }

            }
//            .feed("input_2", tensorInput)
//            .fetch("sequential_1/dense_4/Softmax")
////            .fetch("sequential_1/dense_4/BiasAdd")
//            .runFirstTensor {
//                println("Output Shape: ${it.shape().joinToString()}")
//                val result: Array<out FloatArray> = it.getFloatArrayOutput(numberOfInputs)
//                result.forEach { println("Result: ${it.joinToString()}") }
//            }
}