package org.yanzhe.robomaster.recodaemon.core.processor;

import org.bytedeco.javacpp.opencv_core.Mat;

import static org.bytedeco.javacpp.opencv_imgproc.*;

public class DefaultImageProcessor implements ImageProcessor {
    @Override
    public Mat process(Mat img) {
        if (img.channels() > 1) cvtColor(img, img, CV_BGR2GRAY);
        threshold(img, img, 127, 1, THRESH_BINARY_INV | THRESH_OTSU);
        return img;
    }
}
