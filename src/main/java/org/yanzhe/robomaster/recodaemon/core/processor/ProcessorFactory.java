package org.yanzhe.robomaster.recodaemon.core.processor;

public class ProcessorFactory {
    public static <T extends ImageProcessor> T getProcessor(Class<T> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
