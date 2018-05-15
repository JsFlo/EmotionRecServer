package com.emotionrec.gcpinference.network

import com.emotionrec.gcpinference.models.GcpPredictionInput
import com.emotionrec.gcpinference.models.GcpPredictionResult
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val API_NAME = "ml"
private const val API_VERSION = "v1"
private const val BASE_URL = "https://${API_NAME}.googleapis.com/${API_VERSION}/"

private const val READ_TIMEOUT_SECONDS = 180L

interface GcpPredictionApi {
    @POST("projects/ml-happy-rec/models/happy_rec_model/versions/v2:predict")
    fun getPredictions(@Body predictionsInput: GcpPredictionInput): Call<GcpPredictionResult>
}

private val retrofit by lazy {
    val googleCredentialAuthenticatorAndInterceptor = GoogleCredentialAuth()
    val okHttpClient = OkHttpClient.Builder()
            .authenticator(googleCredentialAuthenticatorAndInterceptor)
            .addNetworkInterceptor(googleCredentialAuthenticatorAndInterceptor)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

val predictionServiceApi by lazy {
    retrofit.create(GcpPredictionApi::class.java)
}