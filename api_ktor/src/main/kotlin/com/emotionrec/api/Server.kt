package com.emotionrec.api


import com.emotionrec.api.common.shouldUseGcp
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.gcpinference.GcpInferenceService
import com.emotionrec.gcpinference.network.GoogleCredentialAuth
import com.emotionrec.localtfinference.LocalInferenceService
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import mu.KotlinLogging
import org.tensorflow.SavedModelBundle
import java.util.*

private val logger = KotlinLogging.logger { }
fun Application.main() {
    val inferenceService = getInferenceService(shouldUseGcp())
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(DefaultHeaders)
    routing {
        get("/ping") {
            logger.debug { "Received ping" }
            call.respondText { "pong" }
        }
        postPrediction(inferenceService)
        postPredictionImage(inferenceService)
    }

}

fun getInferenceService(shouldUseGcp: Boolean): InferenceService {
    return if(shouldUseGcp) getGcpInferenceService() else getLocalInferenceService()
}

fun getLocalInferenceService(): LocalInferenceService {
    logger.debug { "Using Local inference" }
    return LocalInferenceService { SavedModelBundle.load("./src/main/resources/1", "serve") }
}

fun getGcpInferenceService(): GcpInferenceService {
    logger.debug { "Using Gcp inference" }
    val GOOGLE_CRED_FILE = "happy_rec_cred.json"

    return GcpInferenceService(GoogleCredential.fromStream(GoogleCredentialAuth::class.java.classLoader.getResourceAsStream(GOOGLE_CRED_FILE),
            GoogleNetHttpTransport.newTrustedTransport()
            , JacksonFactory.getDefaultInstance())
            .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform")))
}

