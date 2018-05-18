package com.emotionrec.tfinference;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

public class JavaUtils {

    @NotNull
    public static float[][] runInference(Session session, Float[][][][] input) {
        Tensor inputTensor = getInputTensor(input);
        Tensor resultTensor = session.runner()
                .feed("input_2", inputTensor)
                .fetch("sequential_1/dense_4/Softmax")
                .run().get(0);

        float[][] result = getFloatArrayOutput(resultTensor, input.length, 7);
        inputTensor.close();
        resultTensor.close();
        return result;

    }

    private static Tensor getInputTensor(Float[][][][] input) {
        float[][][][] realInputs = new float[input.length][48][48][3];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < 48; j++) {
                for (int k = 0; k < 48; k++) {
                    for (int l = 0; l < 3; l++) {
                        realInputs[i][j][k][l] = input[i][j][k][l];
                    }
                }
            }
        }
        return Tensor.create(realInputs);
    }

    @NotNull
    private static float[][] getFloatArrayOutput(Tensor tensor, int dim1, int dim2) {
        float[][] outputArr = new float[dim1][dim2];
        tensor.copyTo(outputArr);
        return outputArr;
    }
}
