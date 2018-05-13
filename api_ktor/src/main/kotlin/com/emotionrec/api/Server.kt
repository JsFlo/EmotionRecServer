package com.emotionrec.api

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.response.respondText
import io.ktor.routing.get
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
        postPrediction()
    }
}