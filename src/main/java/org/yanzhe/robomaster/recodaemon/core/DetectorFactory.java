package org.yanzhe.robomaster.recodaemon.core;

public class DetectorFactory {
    public static <T extends ImageClassifier> ImageClassifier provide(Class<T> cls, String modelDir) {
        try {
            return cls.getDeclaredConstructor(String.class).newInstance(modelDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
