package com.emotionrec.gcpinference

import arrow.core.Try
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.gcpinference.models.GcpPredictionResult
import com.emotionrec.gcpinference.models.toGcpPredictionInput
import com.emotionrec.gcpinference.network.GcpPredictionApi
import com.emotionrec.gcpinference.network.predictionServiceApi
import retrofit2.Response

class GcpInferenceService(private val predictionApi: GcpPredictionApi = predictionServiceApi) : InferenceService {
    override fun getPrediction(inferenceInputs: List<InferenceInput>): Try<List<PredictionGroup>> {
        val response: Response<GcpPredictionResult>? = predictionApi.getPredictions(inferenceInputs.toGcpPredictionInput()).execute()
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