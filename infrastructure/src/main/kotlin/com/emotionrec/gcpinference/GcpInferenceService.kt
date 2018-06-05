package com.emotionrec.gcpinference

import arrow.core.Try
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.gcpinference.models.GcpPredictionResult
import com.emotionrec.gcpinference.models.toGcpPredictionInput
import com.emotionrec.gcpinference.network.GcpPredictionApi
import com.emotionrec.gcpinference.network.GoogleCredentialAuth
import com.emotionrec.gcpinference.network.getGcpPredictionServiceApi
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import retrofit2.Response

class GcpInferenceService(googleCredential: GoogleCredential) : InferenceService {

    private val gcpPredictionApi: GcpPredictionApi = getGcpPredictionServiceApi(GoogleCredentialAuth(googleCredential))

    override fun getPrediction(inferenceInputs: List<InferenceInput>): Try<List<PredictionGroup>> {
        val response: Response<GcpPredictionResult>? = gcpPredictionApi.getPredictions(inferenceInputs.toGcpPredictionInput()).execute()
        return if (response?.isSuccessful == true) {
            val predictionGroups = response.body()?.toPredictionGroups()
            if (predictionGroups != null) {
                Try.just(predictionGroups)
            } else {
                Try.raise(Throwable("Success but body null ?"))
            }
        } else {
            val error = response?.errorBody()?.string()
            println(error)
            Try.raise(Throwable(error))
        }

    }
}