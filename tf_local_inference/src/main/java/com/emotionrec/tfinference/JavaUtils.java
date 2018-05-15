package com.emotionrec.tfinference;

import kotlin.jvm.JvmStatic;
import org.jetbrains.annotations.NotNull;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.util.Arrays;

public class JavaUtils {

    @NotNull @JvmStatic
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

    @NotNull @JvmStatic
    public static String getSomething() {
        return "something";
    }

    public static float[] executeInceptionGraph(byte[] graphDef, Tensor<Float> image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor<Float> result =
                         s.runner().feed("input", image)
                                 .fetch("output").run().get(0).expect(Float.class)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                return result.copyTo(new float[1][nlabels])[0];
            }
        }
    }
}
