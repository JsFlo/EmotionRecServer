package com.emotionrec.validationclient.datainput

import com.emotionrec.domain.models.Emotion
import com.emotionrec.validationclient.models.EmotionPixelsData
import com.emotionrec.validationclient.models.RowData
import java.io.BufferedReader
import java.io.FileReader


const val HEADER_EMOTION = "emotion"
const val HEADER_PIXELS = "pixels"
const val CSV_FILE_NAME = "fer2013.csv"


fun getFormattedInput(numberOfInputs: Int): List<EmotionPixelsData> {
    val rowDataList = getPartialInputData(numberOfInputs)

    val emotionPixelsDataList = rowDataList.map {
        EmotionPixelsData(emotionRowToEmotionTransformer(it.emotion),
                pixelRowToArrayOfFloats(it.pixels).map { it.map { it -> arrayOf(it, it, it) } })// rgb with same values
    }

    return emotionPixelsDataList
}


fun Int?.toEmotion(): Emotion {
    return if (this != null && this in 0..6)
        Emotion.emotionValues[this]
    else
        Emotion.VULCAN
}

//0=Angry, 1=Disgust, 2=Fear, 3=Happy, 4=Sad, 5=Surprise, 6=Neutral).
fun emotionRowToEmotionTransformer(emotionRow: String): Emotion {
    return emotionRow.toIntOrNull().toEmotion()
}

// 48 x 48, /255
fun pixelRowToArrayOfFloats(pixels: String): Array<Array<Float>> {
    val pixelArray = pixels.split(" ")
    // should be 2304

    val floatArrayList = mutableListOf<Array<Float>>()
    for (i in 0 until 48) {
        val rowFloatArray = FloatArray(48)
        for (j in 0 until 48) {
            rowFloatArray[j] = pixelArray[(i * 48) + j].toFloat() / 255
        }
        floatArrayList.add(rowFloatArray.toTypedArray())
    }
    return floatArrayList.toTypedArray()
}


fun getAllInputData(): List<RowData> {
    return readData { _, rawRow ->
        rawRow?.isNotEmpty() == true
    }
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
