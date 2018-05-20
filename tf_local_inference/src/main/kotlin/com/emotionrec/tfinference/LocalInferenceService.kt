package com.emotionrec.tfinference

import arrow.core.Try
import com.emotionrec.domain.models.InferenceInput
import com.emotionrec.domain.models.PredictionGroup
import com.emotionrec.domain.models.toPredictionGroup
import com.emotionrec.domain.service.InferenceService
import com.emotionrec.tfinference.models.toLocalInferenceInput
import org.tensorflow.SavedModelBundle

class LocalInferenceService : InferenceService {

    override fun getPrediction(inferenceInputs: List<InferenceInput>): Try<List<PredictionGroup>> {
        // TODO: load on every request ?
        println("user dir: " + System.getProperty("user.dir"))
        val load: SavedModelBundle = SavedModelBundle.load("./1", "serve")
        return try {
            val inferenceResult = JavaUtils.runInference(load.session(), inferenceInputs.toLocalInferenceInput())
            val result = inferenceResult.map { it.toTypedArray().toPredictionGroup(true) }
            Try.just(result)
        } catch (e: Exception) {
            Try.raise(e)
        } finally {
            load.close()
        }
    }

}