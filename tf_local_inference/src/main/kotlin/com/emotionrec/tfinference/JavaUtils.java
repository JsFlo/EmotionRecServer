package com.emotionrec.tfinference;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.Tensor;

public class JavaUtils {

    @NotNull
    public static float[][] getFloatArrayOutput(Tensor tensor, int dim1, int dim2) {
        float[][] outputArr = new float[dim1][dim2];
        tensor.copyTo(outputArr);
        return outputArr;
    }
}
