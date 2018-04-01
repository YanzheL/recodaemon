package org.yanzhe.robomaster.recodaemon.core.classifier;

import org.yanzhe.robomaster.recodaemon.core.utils.CoreUtils;

import java.util.Arrays;
import java.util.Collections;

public abstract class AbstractImageClassifier implements ImageClassifier {
    @Override
    public int predict(byte[] imgData) {
        int[] predictions = new int[1];
        return predict(Arrays.asList(imgData), 1, predictions)[0];
    }

    @Override
    public int[] predict(Iterable<byte[]> imgBatch, int batchSize, int[] predictions) {
        float[][] probs = new float[batchSize][10];
        proba(imgBatch, batchSize, probs);
        //    int[] predictions = new int[batchSize];
        for (int i = 0; i < batchSize; ++i) predictions[i] = (int) CoreUtils.findMaxPos(probs[i])[0];
        return predictions;
    }

    @Override
    public float[] proba(byte[] imgData, float[][] dst) {
        return proba(Collections.singletonList(imgData), 1, dst)[0];
    }

    @Override
    public float[][] proba(byte[][] imgBatch, int batchSize, float[][] dst) {
        return proba(Arrays.asList(imgBatch), batchSize, dst);
    }
    //    @Override
//    public abstract float[][] proba(Iterable<byte[]> imgBatch, int batchSize, float[][] dst);

//    @Override
//    public abstract void close();
}
