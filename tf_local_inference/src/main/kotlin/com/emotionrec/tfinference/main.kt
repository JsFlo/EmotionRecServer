package com.emotionrec.tfinference

import com.emotionrec.domain.utils.ValidationInputRetrieval
import com.emotionrec.tfinference.exts.runFirstTensor
import org.tensorflow.SavedModelBundle
import org.tensorflow.Tensor


fun main(args: Array<String>) {
    val load = SavedModelBundle.load("./1", "serve")

    val inputRet  = ValidationInputRetrieval()
    val result = inputRet.getFormattedInput(1)
    val infrenceInput = result[0].first
    val input = infrenceInput.images.map { it.map { arrayOf(it.r, it.b, it.g) } }
    val betterInput: Array<Array<Array<Float>>> = input.map { it.toTypedArray() }.toTypedArray()
//        FloatBuffer.wrap(inpu)
//        val tensorInput = Tensor.create(arrayOf<Long>(48, 48, 3), )
    val tensorInput = Tensor.create(arrayOf(betterInput))
    load.session().runner()
            .feed("input_2", tensorInput)
            .fetch("sequential_1/dense_4/Softmax")
            .runFirstTensor {
                println("Hi")
            }
//    load.graph().use { graph ->
//        //        graph.operations().forEach { println("Name: ${it.name()}, ${it.type()} ") }
//        // input "input_2"
//        // sequential_1/dense_4/Softmax
//        val result = getFormattedInput(1)
//        val infrenceInput = result[0].first
//        val input = infrenceInput.images.map { it.map { arrayOf(it.r, it.b, it.g) } }
//        val betterInput: Array<Array<Array<Float>>> = input.map { it.toTypedArray() }.toTypedArray()
////        FloatBuffer.wrap(inpu)
////        val tensorInput = Tensor.create(arrayOf<Long>(48, 48, 3), )
//        val tensorInput = Tensor.create(betterInput)
//        Session(graph).use { sess ->
//            sess.runner()
//                    .feed("input_2", tensorInput)
//                    .fetch("sequential_1/dense_4/Softmax")
//                    .runFirstTensor {
//                        println("Hi")
//                    }
////                    .feed("input_2", tensorInput)
////                    .fetch("sequential_1/dense_4/Softmax")
////                    .run()[0]
//
////            val outputArr = FloatArray(7).toTypedArray()
////            sessResult.copyTo(outputArr)
////            println("Oh shi: $outputArr")
//        }
//    }
//    Graph().use { graph ->
//
//        // Creates a graph for y = a (placeholder) + b (placeholder)
//        val a = graph.addPlaceholder("a", DataType.FLOAT)
//        val b = graph.addPlaceholder("b", DataType.FLOAT)
//        val y = graph.Operation("y", OperationType.ADD, a, b)
//
//        Session(graph).use { sess ->
//
//            val ta = Tensor.create(10f)
//            val tb = Tensor.create(10f)
//            sess.runner()
//                    .feed(a, ta)
//                    .feed(b, tb)
//                    .fetch(y)
//                    .runFirstTensor {
//                        println("${ta.floatValue()} + ${tb.floatValue()} = ${it.floatValue()}")
//                    }
//            ta.close()
//            tb.close()
//        }
//    }
}