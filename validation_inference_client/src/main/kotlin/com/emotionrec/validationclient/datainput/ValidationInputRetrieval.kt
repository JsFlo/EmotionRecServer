package com.emotionrec.validationclient.datainput

import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.RGB
import com.emotionrec.domain.models.toEmotion
import com.emotionrec.validationclient.models.RowData
import java.io.BufferedReader
import java.io.FileReader


const val HEADER_EMOTION = "emotion"
const val HEADER_PIXELS = "pixels"
const val CSV_FILE_NAME = "validation_inference_client/fer2013.csv"


fun getFormattedInput(numberOfInputs: Int): List<Pair<InferenceInput, Emotion>> {
    return getPartialInputData(numberOfInputs).map {
        Pair(pixelRowToArrayOfFloats(it.pixels), emotionRowToEmotionTransformer(it.emotion))
    }
}

//0=Angry, 1=Disgust, 2=Fear, 3=Happy, 4=Sad, 5=Surprise, 6=Neutral).
fun emotionRowToEmotionTransformer(emotionRow: String): Emotion {
    return emotionRow.toIntOrNull().toEmotion()
}

// 48 x 48, /255
fun pixelRowToArrayOfFloats(pixels: String): InferenceInput {
    val pixelArray = pixels.split(" ")
    check(pixelArray.size == 2304)

    val rowRgbList = mutableListOf<List<RGB>>()
    for (i in 0 until 48) {
        val rowRgb = mutableListOf<RGB>()
        for (j in 0 until 48) {
            val pixelValue = pixelArray[(i * 48) + j].toFloat() / 255
            val rgb = RGB(arrayOf(pixelValue, pixelValue, pixelValue))// rgb with same values
            rowRgb.add(rgb)
        }
        rowRgbList.add(rowRgb)
    }
    return InferenceInput(rowRgbList)
}

fun getPartialInputData(numberOfInputs: Int): List<RowData> {
    return readData { index, _ ->
        index < numberOfInputs
    }
}

private fun readData(dataRowsPredicate: (index: Int, rawRow: String?) -> Boolean): List<RowData> {
    val inputData: MutableList<RowData> = ArrayList()
    BufferedReader(FileReader(CSV_FILE_NAME)).use {
        // headers
        val headers = it.readLine()
        val headersMap: Map<String, Int> = headers.split(",") // split string into array
                .mapIndexed { index, s -> Pair(s, index) } // transform to add index
                .associateBy({ it.first }, { it.second }) // make map with string, index
        println("Headers: $headersMap")

        // header index
        val emotionIndex = headersMap[HEADER_EMOTION]!!
        val pixelIndex = headersMap[HEADER_PIXELS]!!

        // main data loop
        var dataRowsIndex = 0
        var dataRow = it.readLine()
        while (dataRowsPredicate(dataRowsIndex, dataRow)) {
            inputData.add(transformInputData(dataRow, emotionIndex, pixelIndex))

            dataRow = it.readLine()
            dataRowsIndex++
        }

    }

    println("Data retrieved: ${inputData.size}")
    if (inputData.isNotEmpty()) {
        println("Example data - first row: ${inputData[0]}")
    }
    return inputData
}

private fun transformInputData(rawRow: String, emotionIndex: Int, pixelIndex: Int): RowData {
    val dataArray = rawRow.split(",")
    return RowData(dataArray[emotionIndex], dataArray[pixelIndex])
}
