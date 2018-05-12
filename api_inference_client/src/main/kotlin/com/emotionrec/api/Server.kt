package com.emotionrec.api

import arrow.core.getOrElse
import arrow.core.recover
import com.emotionrec.api.models.PredictionResult
import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.ErrorRate
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.gcpinference.GcpInferenceService
import com.emotionrec.gcpinference.models.GcpPredictionInput
import com.emotionrec.gcpinference.models.GcpPredictionInstance
import com.emotionrec.gcpinference.models.toGcpPredictionInput
import com.emotionrec.gcpinference.models.toGcpPredictionInstance
import com.emotionrec.validationclient.datainput.pixelRowToArrayOfFloats
import com.google.gson.JsonObject
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main(args: Array<String>) {
    val inferenceService = GcpInferenceService()
    embeddedServer(Netty, 8787) {
        routing {
            get("/") {
                call.respondText("Hi", ContentType.Text.Plain)
            }
            get("/demo") {
                call.respondText { "Hello World" }
            }
            post("/prediction") {
                val receive: Parameters = call.receive<Parameters>()
                val arrValue: String? = receive.get("arr")
                arrValue?.let {
                    val inferenceInput = pixelRowToArrayOfFloats(it)
                    val result = inferenceService.getPrediction(listOf(inferenceInput))
                    val resultValue: Any = result.getOrElse { it }
                    when (resultValue) {
                        is List<*> -> call.respondText { getJsonPredictionResponse(resultValue as List<PredictionGroup>).toString() }
                        is Throwable -> call.respondText { "throw" }
                    }
                }

            }
        }
    }.start(true)
}


fun getJsonPredictionResponse(predictionGroups: List<PredictionGroup>): PredictionResult {

    return predictionGroups[0].let {
        val guessedPrediction = it.sortedPredictions.first()
        PredictionResult(
                it.findEmotionPercentage(Emotion.ANGRY),
                it.findEmotionPercentage(Emotion.DISGUST),
                it.findEmotionPercentage(Emotion.FEAR),
                it.findEmotionPercentage(Emotion.HAPPY),
                it.findEmotionPercentage(Emotion.SAD),
                it.findEmotionPercentage(Emotion.SURPRISE),
                it.findEmotionPercentage(Emotion.NEUTRAL),
                guessedPrediction.emotion.name,
                guessedPrediction.emotion.ordinal
        )

    }
}

fun PredictionGroup.findEmotionPercentage(emotion: Emotion): Int {
    val probability: Double = this.sortedPredictions.find { it.emotion == emotion }?.probability ?: 0.0
    return (probability * 100).toInt()
}

