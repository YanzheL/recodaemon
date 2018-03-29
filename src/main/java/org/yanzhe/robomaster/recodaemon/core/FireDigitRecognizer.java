package org.yanzhe.robomaster.recodaemon.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.yanzhe.robomaster.recodaemon.core.utils.CoreUtils;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.CV_8UC3;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class FireDigitRecognizer extends AbstractImageClassifier {

    protected static Logger logger = LogManager.getLogger(FireDigitRecognizer.class);
    private static PureDigitRecognizer recognizer;

    public FireDigitRecognizer(String modelDir) {
        if (recognizer == null)
            recognizer = new PureDigitRecognizer(modelDir);
    }

    @Override
    public float[][] proba(Iterable<byte[]> imgBatch, int batchSize, float[][] dst) {
        List<Mat> mats = new ArrayList<>();

        for (byte[] imgData : imgBatch) {
            Mat img = new Mat(28, 28, CV_8UC3);
            img.data().put(imgData);
            if (img.channels() > 1)
                cvtColor(img, img, CV_BGR2GRAY);
            threshold(img, img, 127, 1, THRESH_BINARY_INV | THRESH_OTSU);
            mats.add(img);
        }

        Iterable<byte[]> newBatch = CoreUtils.toBatch(mats);
//        CoreUtils.showImgMat(newBatch);
        return recognizer.proba(newBatch, batchSize, dst);
    }

    @Override
    public void close() {
        recognizer.close();
    }


}
