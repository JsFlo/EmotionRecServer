package com.emotionrec.api

import arrow.core.getOrElse
import com.emotionrec.api.models.PredictionResult
import com.emotionrec.domain.models.Emotion
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.gcpinference.GcpInferenceService
import com.emotionrec.validationclient.datainput.pixelRowToArrayOfFloats
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
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
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        routing {
            get("/ping") {
                call.respondText{"pong"}
            }
            post("/prediction") {
                val receive: Parameters = call.receive<Parameters>()
                val arrValue: String? = receive.get("arr")
                arrValue?.let {
                    val inferenceInput = pixelRowToArrayOfFloats(it)
                    val result = inferenceService.getPrediction(listOf(inferenceInput))
                    val resultValue: Any = result.getOrElse { it }
                    when (resultValue) {
                        is List<*> -> call.respond(getJsonPredictionResponse(resultValue as List<PredictionGroup>))
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

