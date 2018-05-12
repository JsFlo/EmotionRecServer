package com.emotionrec.api

import com.emotionrec.api.models.PredictionResult
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
                call.respondText { "pong" }
            }
            post("/prediction") {
                val receive = call.receive<Parameters>()
                val arrValue: String? = receive.get("image_array")
                val delimeter: String? = receive.get("delimeter")
                arrValue?.let {
                    val inferenceInput = pixelRowToArrayOfFloats(it, delimeter ?: " ")
                    val result = inferenceService.getPrediction(listOf(inferenceInput))
                    result.fold(
                            { call.respondText { "throw: $it" } },
                            { call.respond(it.toPredictionResult()) }
                    )
                }

            }
        }
    }.start(true)
}


fun List<PredictionGroup>.toPredictionResult(): PredictionResult {
    return PredictionResult(this[0].sortedPredictions, this[0].sortedPredictions[0])
}

