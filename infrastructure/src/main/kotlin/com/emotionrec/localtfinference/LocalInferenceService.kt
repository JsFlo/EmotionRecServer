package com.emotionrec.localtfinference

import arrow.core.Try
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.models.toPredictionGroup
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.localtfinference.models.toLocalInferenceInput
import org.tensorflow.SavedModelBundle

class LocalInferenceService(val getSavedModelBundle: () -> SavedModelBundle) : InferenceService {

    override fun getPrediction(inferenceInputs: List<InferenceInput>): Try<List<PredictionGroup>> {
        // TODO: load on every request ?
        val savedModelBundle: SavedModelBundle = getSavedModelBundle()

        return try {
            val inferenceResult = JavaUtils.runInference(savedModelBundle.session(), inferenceInputs.toLocalInferenceInput())
            val result = inferenceResult.map { it.toTypedArray().toPredictionGroup(false) }
            Try.just(result)
        } catch (e: Exception) {
            Try.raise(e)
        } finally {
            savedModelBundle.close()
        }
    }

}