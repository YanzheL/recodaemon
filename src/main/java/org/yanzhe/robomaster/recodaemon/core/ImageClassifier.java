package org.yanzhe.robomaster.recodaemon.core;

public interface ImageClassifier extends AutoCloseable {
    int predict(byte[] imgData);

    int[] predict(Iterable<byte[]> imgBatch, int batchSize, int[] predictions);

    float[] proba(byte[] imgData, float[][] dst);

    float[][] proba(Iterable<byte[]> imgBatch, int batchSize, float[][] dst);

    float[][] proba(byte[][] imgBatch, int batchSize, float[][] dst);

}
