package org.yanzhe.robomaster.recodaemon.core.processor;

import org.bytedeco.javacpp.opencv_core.Mat;

public interface ImageProcessor {
    Mat process(Mat src);
}
