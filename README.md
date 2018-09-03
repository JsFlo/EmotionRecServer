# EmotionRecServer
Ktor server for providing emotion recognition. This is done through a trained TensorFlow model that is either located locally or uploaded to Google Cloud Platform [ML Engine](https://cloud.google.com/ml-engine/).

I wrote an [article covering the specific TensorFlow model I trained](https://medium.com/@jsflo.dev/training-a-tensorflow-model-to-recognize-emotions-a20c3bcd6468).

## Modes of inference
* Local Inference
* GCP Inference [ML Engine]

To be able to choose which inference mode the server will use you will have to set a property in the application configuration found in  `/api_ktor/src/main/resources/application.conf`.

```
ktor {
    ...
    application {
        ...
        gcp = false
    }
}

```

Setting *gcp* to *false* will configure the server to use the local inference otherwise it will use the model hosted in GCP.

### Local Inference
LocalInference will use the Java TensorFlow Api to load the trained model.

To be able to use local inference you first have to point the Server to the location of your model.

**TODO**: Currently this is done through a static variable but this should be moved to a config file.

`/api_ktor/src/main/kotlin/com/emotionrec/api/Server.kt`
```java
val LOCAL_INF_MODEL = "./src/main/resources/1"
val LOCAL_INF_TAG = "serve"
```

The first is the location of the model relative to the project and the other is the tag used while saving the model through the [SavedModelApi](https://medium.com/@jsflo.dev/saving-and-loading-a-tensorflow-model-using-the-savedmodel-api-17645576527).

### GCP ML Engine Inference
In order to use the the predictions coming from the model hosted on GCP you will have to upload a saved model to your GCP account. You will need to have two things.
* Path to your model
* Credential File

#### Path to your model
`"projects/ml-happy-rec/models/happy_rec_model/versions/v2:predict"`

This is a hardcoded path found in the `RetrofitNetwork.kt` and should be changed to point to your specific model.

#### Credentials
**TODO**: Value should be set through config

`/api_ktor/src/main/kotlin/com/emotionrec/api/Server.kt`
```java
val GOOGLE_CRED_FILE = "happy_rec_cred.json"
```

This is currently handled through the use of the Google credential file given through GCP.

## Running the Server
To run the api_ktor application: `./gradlew api_ktor:run`
which will use the default settings (port) defined in the application.conf file.

```
ktor {
    deployment {
        port = 8378
        environment = development
        watch = [ emotionrec ]
    }

    application {
        id = emotionrec
        modules = [com.emotionrec.api.ServerKt.main]
        gcp = false
    }
}
```
### Simple Api
**GET** /ping
* Used for sanity checks and returns "pong"

**POST** /prediction
* Accepts [PostPredictionData].
* Expects the [PostPredictionData.image_array]:
    * to be an array of size **2304**
    * String array separated by a delimiter [PostPredictionData.delimiter] (default: [DEFAULT_DELIMITER])

* Responds with [PredictionError] or [PredictionResponse]

**POST** /predictionImage
* Accepts mutlipart file image upload
* Responds with [PredictionError] or [PredictionResponse]

#### PredictionResponse
```json
{
    "sortedPredictions": [
        {
            "probability": 0.99999285,
            "emotion": "ANGRY"
        },
        {
            "probability": 0.0000035176417,
            "emotion": "SAD"
        },
        {
            "probability": 0.0000018190486,
            "emotion": "FEAR"
        },
        {
            "probability": 0.0000018007337,
            "emotion": "NEUTRAL"
        },
        {
            "probability": 1.873281e-8,
            "emotion": "HAPPY"
        },
        {
            "probability": 3.4072745e-11,
            "emotion": "DISGUST"
        },
        {
            "probability": 2.9763858e-12,
            "emotion": "SURPRISE"
        }
    ],
    "guessedPrediction": {
        "probability": 0.99999285,
        "emotion": "ANGRY"
    }
}
```
