package org.yanzhe.robomaster.recodaemon.core.utils;

import org.bytedeco.javacpp.BytePointer;
import org.yanzhe.robomaster.recodaemon.net.proto.ImageProto.Image;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.CV_8UC;
import static org.bytedeco.javacpp.opencv_core.Mat;

public class CoreUtils {
  public static float[] findMaxPos(float[] buf) {
    int maxPos = -1;
    float max = 0;
    for (int i = 0; i < buf.length; ++i) {
      float dt = buf[i];
      if (dt > max) {
        max = dt;
        maxPos = i;
      }
    }
      return new float[]{maxPos, max};
  }

  public static void writeToInputBuf(Iterable<byte[]> imgBatch, FloatBuffer buf) {
    for (byte[] img : imgBatch) {
      writeToInputBuf(img, buf);
    }
  }

  public static void writeToInputBuf(byte[][] imgBatch, FloatBuffer buf) {
    writeToInputBuf(Arrays.asList(imgBatch), buf);
  }

  public static void writeToInputBuf(byte[] imgData, FloatBuffer buf) {
    for (byte b : imgData) {
      float val = (float) (0xff & b);
      buf.put(val);
    }
  }

  public static void showImgMat(Iterable<byte[]> imgBatch) {
    for (byte[] imgData : imgBatch) {
      System.out.println();
      showImgMat(imgData);
    }
  }

  public static void showImgMat(Image imgData) {
    showImgMat(imgData.getData().toByteArray());
  }

    public static void showImgMat(Mat img) {
        byte[] data = new byte[img.rows() * img.cols() * img.channels()];
        img.data().get(data);
        showImgMat(data);
    }

  public static void showImgMat(byte[] imgData) {
      System.out.println();
    int c = 0;
    for (byte b : imgData) {
      ++c;
        System.out.format("%4d", 0xff & b);
      if (c % 28 == 0) System.out.println();
    }
  }

  public static void showImgMat(FloatBuffer buf, int len) {
    //    System.out.println();
    int c = 0;
    for (int i = 0; i < len; ++i) {
      c++;
        System.out.format("%4d", (int) buf.get(i));
      if (c % 28 == 0) System.out.println();
    }
  }

  public static Iterable<byte[]> toBatch(Iterable<Mat> imgs) {
    List<byte[]> batch = new ArrayList<>();
    for (Mat img : imgs) {
      byte[] dt = new byte[img.rows() * img.cols()];
      img.data().get(dt);
      batch.add(dt);
    }
    return batch;
  }

  public static Mat toMat(Image imgProto) {
    return new Mat(
            imgProto.getRows().getValue(),
            imgProto.getCols().getValue(),
            CV_8UC(imgProto.getChannels().getValue()),
            new BytePointer(imgProto.getData().toByteArray()));
  }
}
