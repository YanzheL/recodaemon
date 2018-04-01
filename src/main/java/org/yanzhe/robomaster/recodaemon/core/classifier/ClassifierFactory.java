package org.yanzhe.robomaster.recodaemon.core.classifier;

public class ClassifierFactory {
    public static <T extends ImageClassifier> T getDetector(Class<T> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
