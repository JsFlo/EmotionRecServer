//package com.emotionrec.api
//
//
//import com.emotionrec.tfinference.LocalInferenceService
//import io.ktor.application.Application
//import io.ktor.application.call
//import io.ktor.application.install
//import io.ktor.features.ContentNegotiation
//import io.ktor.features.DefaultHeaders
//import io.ktor.gson.gson
//import io.ktor.response.respondText
//import io.ktor.routing.get
//import io.ktor.routing.routing
//import mu.KotlinLogging
//
//private val logger = KotlinLogging.logger { }
//fun Application.main() {
//    val inferenceService = LocalInferenceService() // GcpInferenceService()
//    install(ContentNegotiation) {
//        gson {
//            setPrettyPrinting()
//        }
//    }
//    install(DefaultHeaders)
//    routing {
//        get("/ping") {
//            logger.debug { "Received ping" }
//            call.respondText { "pong" }
//        }
//        postPrediction(inferenceService)
//        postPredictionImage(inferenceService)
//    }
//
//}