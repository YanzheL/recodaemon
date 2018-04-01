package org.yanzhe.robomaster.recodaemon.core.processor;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.yanzhe.robomaster.recodaemon.core.utils.CachedSingleton;

import java.util.HashMap;
import java.util.Map;

public interface ImageProcessor extends CachedSingleton {

    Mat process(Mat src);

    static void main(String[] args) {
        Map a = new HashMap();
        System.out.println(a.getClass());
    }
}
