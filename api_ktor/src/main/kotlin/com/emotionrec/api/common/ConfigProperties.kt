package com.emotionrec.api.common

import io.ktor.application.Application

const val CONFIG_GCP = "ktor.application.gcp"
fun Application.shouldUseGcp(): Boolean {
    val prop = this.environment.config
            .propertyOrNull(CONFIG_GCP)

    return prop?.getString()?.toBoolean()
            ?: false
}