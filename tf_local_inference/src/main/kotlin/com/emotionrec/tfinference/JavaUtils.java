package com.emotionrec.tfinference;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.Tensor;

public class JavaUtils {

    @NotNull
    public static float[][] getFloatArrayOutput(Tensor tensor, int dim1, int dim2) {
        float[][] outputArr = new float[dim1][dim2];
        tensor.copyTo(outputArr);
        System.out.println("hi");
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                System.out.println("" + outputArr[i][j]);
            }
        }
        return outputArr;
    }
}
