package com.emotionrec.api.responses

import com.emotionrec.domain.models.Prediction

data class PredictionResponse(val sortedPredictions: List<Prediction>,
                              val guessedPrediction: Prediction)