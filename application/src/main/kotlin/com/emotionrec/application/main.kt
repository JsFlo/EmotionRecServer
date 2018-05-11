package com.emotionrec.application

import com.emotionrec.domain.service.InferenceServerClient
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.gcpinference.GcpInferenceService
import com.emotionrec.validationclient.ValidationInputInference

fun main(args: Array<String>) {
    val inferenceService: InferenceService = GcpInferenceService()
    val inferenceClient: InferenceServerClient = ValidationInputInference(inferenceService)
}