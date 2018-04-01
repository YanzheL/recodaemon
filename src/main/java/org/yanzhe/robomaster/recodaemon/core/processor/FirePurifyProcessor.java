package org.yanzhe.robomaster.recodaemon.core.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.opencv_core.Mat;

import static org.yanzhe.robomaster.recodaemon.core.processor.nativeimpl.NativeProcessorLibrary.NativeFireProcessor;

public class FirePurifyProcessor implements ImageProcessor {
    //      public static final NativeFireProcessor processor=new NativeFireProcessor();
    private static Logger logger = LogManager.getLogger(FirePurifyProcessor.class);
    private Mat result = new Mat();

    //    private static Mat img = imread("/home/trinity/CLionProjects/Num_opti/5.png", 0);
    @Override
    public Mat process(Mat src) {


        long t1 = System.currentTimeMillis();
        NativeFireProcessor.process(src, result);
//    imshow("r", result);
//    waitKey(0);
        //      processor.process(src, result);
        logger.debug("Native call time = {} ms", System.currentTimeMillis() - t1);

        return result;
    }
}
