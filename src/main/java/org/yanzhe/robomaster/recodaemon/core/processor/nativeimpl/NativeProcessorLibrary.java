package org.yanzhe.robomaster.recodaemon.core.processor.nativeimpl;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.*;
import org.bytedeco.javacpp.opencv_core.Mat;

import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

@Platform(
        include = "/home/trinity/CLionProjects/Num_opti/NativeProcessorLibrary.h",
        define = "DEBUG_CHECK_SRC_IMG",
        link = "opencv_world"

)
@Namespace("NativeProcessorLibrary")
public class NativeProcessorLibrary {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        Mat img = imread("/home/trinity/CLionProjects/Num_opti/5.png", 0);
        //      NativeFireProcessor p=new NativeFireProcessor();
        for (int i = 0; i < 2000; ++i) {
            Mat result = new Mat();
            long tt = System.currentTimeMillis();
            NativeFireProcessor.process(img, result);
            System.out.println(System.currentTimeMillis() - tt);
            //        p.process(img, result);
        }
        System.out.format("Time used = %d ms", System.currentTimeMillis() - t1);
        //    imshow("", result);
        //    waitKey(0);
    }

    public static class NativeFireProcessor extends Pointer {
        static {
            //        System.loadLibrary("jniNativeProcessorLibrary");
            Loader.load();
        }

        public NativeFireProcessor() {
            allocate();
        }

        public static native void process(
                @Const @ByRef @Cast("Mat*") Pointer src, @ByRef @Cast("Mat*") Pointer result);

        private native void allocate();
    }
}
