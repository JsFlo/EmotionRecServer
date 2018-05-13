package com.emotionrec.api

import arrow.core.Either
import com.emotionrec.gcpinference.GcpInferenceService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.main() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(DefaultHeaders)
    routing {
        get("/ping") {
            call.respondText { "pong" }
        }
        post("/prediction") {
            val receive = call.receive<Parameters>()
            val result = predictionInput(receive["image_array"], receive["delimeter"] ?: " ", GcpInferenceService())
            when (result) {
                is Either.Right -> call.respond(result.b)
                is Either.Left -> call.respond(result.a)
            }
        }
    }
}