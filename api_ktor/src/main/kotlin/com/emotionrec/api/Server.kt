package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.api.models.PredictionResult
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.gcpinference.GcpInferenceService
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
                val result = predictionInput(receive["image_array"], receive["delimeter"] ?: " ", inferenceService)
                when (result) {
                    is Either.Right -> call.respond(result.b)
                    is Either.Left -> call.respondText { "throw: ${result.a}" }
                }
            }
        }
    }.start(true)
}