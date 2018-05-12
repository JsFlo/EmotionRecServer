package com.emotionrec.api.models

import com.emotionrec.domain.models.Prediction

data class PredictionResult(val sortedPredictions: List<Prediction>,
                            val guessedPrediction: Prediction)