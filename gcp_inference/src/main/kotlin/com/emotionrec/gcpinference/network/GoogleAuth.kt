package com.emotionrec.gcpinference.network

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import okhttp3.*
import java.io.FileInputStream
import java.util.*

private const val GOOGLE_CRED_FILE = "happy_rec_cred.json"

val googleCredentialFromFile: GoogleCredential by lazy {
    GoogleCredential.fromStream(GoogleCredentialAuth::class.java.classLoader.getResourceAsStream(GOOGLE_CRED_FILE),
            GoogleNetHttpTransport.newTrustedTransport()
            , JacksonFactory.getDefaultInstance())
            .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"))
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class GoogleCredentialAuth(private val googleCredential: GoogleCredential = googleCredentialFromFile) : Authenticator, Interceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val AUTHORIZATION_VALUE_PREFIX = "Bearer "
    }

    private var gAccessToken: String? = null

    // received a 401
    override fun authenticate(route: Route?, response: Response?): Request? {
        refreshAccessToken()

        // Add new header to rejected request and retry it
        return response?.request().newRequestWithGoogleAuth(gAccessToken)
    }

    override fun intercept(chain: Interceptor.Chain?): Response? {
        if (gAccessToken == null) {
            refreshAccessToken()
        }
        return chain?.proceed(chain.request().newRequestWithGoogleAuth(gAccessToken))
    }

    private fun refreshAccessToken() {
        gAccessToken = googleCredential.refreshAndGetAccessToken()
    }

    private fun GoogleCredential.refreshAndGetAccessToken(): String {
        println("Refresh and get access token called. Success: ${refreshToken()}")
        return accessToken
    }

    private fun Request?.newRequestWithGoogleAuth(accessToken: String?): Request? {
        return this?.newBuilder()
                ?.addHeader(AUTHORIZATION_HEADER, AUTHORIZATION_VALUE_PREFIX + accessToken)
                ?.build()
    }

}