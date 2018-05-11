package com.emotionrec.gcpinference

import arrow.core.Try
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.gcpinference.models.GcpPredictionResult
import com.emotionrec.gcpinference.models.toGcpPredictionInput
import com.emotionrec.gcpinference.network.GcpPredictionApi
import com.emotionrec.gcpinference.network.predictionServiceApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GcpInferenceService(val predictionApi: GcpPredictionApi = predictionServiceApi) : InferenceService {
    override fun getPrediction(inferenceInputs: List<InferenceInput>, predictionResult: (Try<List<PredictionGroup>>) -> Unit) {
        predictionApi.getPredictions(inferenceInputs.toGcpPredictionInput())
                .enqueue(object : Callback<GcpPredictionResult> {
                    override fun onResponse(call: Call<GcpPredictionResult>?, response: Response<GcpPredictionResult>?) {
                        println("Successful:  ${response?.isSuccessful}")
                        if (response?.isSuccessful == true) {
                            val predictionGroups = response.body()?.toPredictionGroups()
                            if (predictionGroups != null) {
                                predictionResult(Try.just(predictionGroups))
                            } else {
                                predictionResult(Try.raise(Throwable("Success but body null ?")))
                            }
                        } else {
                            val error = response?.errorBody()?.string()
                            println(error)
                            predictionResult(Try.raise(Throwable(error)))
                        }


                    }

                    override fun onFailure(call: Call<GcpPredictionResult>?, t: Throwable?) {
                        println("failure $t")
                        predictionResult(Try.raise(t!!))
                    }

                })
    }
}