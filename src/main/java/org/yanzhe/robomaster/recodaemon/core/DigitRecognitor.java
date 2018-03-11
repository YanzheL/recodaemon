package org.yanzhe.robomaster.recodaemon.core;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.yanzhe.robomaster.recodaemon.utils.MnistReader;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class DigitRecognitor implements AutoCloseable {
  private Session sess;
  private final int imgSize;
  private Tensor keepProb;
  protected float[] inputSpace;
  public DigitRecognitor(String modelDir) {
    this(modelDir, "serve", 28);
  }

  public DigitRecognitor(String modelDir, String tags, int imgSize) {
    this.sess = SavedModelBundle.load(modelDir, tags).session();
    this.imgSize = imgSize;
    float[] kp = new float[1024];
    Arrays.fill(kp, 1f);
    this.keepProb = Tensor.create(new long[] {1, 1024}, FloatBuffer.wrap(kp));
    inputSpace = new float[9 * imgSize * imgSize];
  }

  public int predict(byte[] imgData, boolean toBinary) {
    int[] predictions = new int[1];
    return predict(Arrays.asList(imgData), toBinary, 1,predictions)[0];
  }

  public static void writeToInput(Iterable<byte[]> imgBatch, FloatBuffer buf, boolean toBinary) {
    for (byte[] img : imgBatch) {
      writeToInput(img, buf, toBinary);
    }
  }

  public int[] predict(Iterable<byte[]> imgBatch, boolean toBinary, int batchSize,int[] predictions) {
    float[][] probs =new float[batchSize][10];
    proba(imgBatch, toBinary, batchSize,probs);
//    int[] predictions = new int[batchSize];
    for (int i = 0; i < batchSize; ++i) {
      predictions[i] = (int) findMaxPos(probs[i])[0];
    }
    return predictions;
  }

  public static void writeToInput(byte[][] imgBatch, FloatBuffer buf, boolean toBinary) {
    for (byte[] img : imgBatch) {
      writeToInput(img, buf, toBinary);
    }
  }

  public float[] proba(byte[] imgData, boolean toBinary, float[][] dst) {
    return proba(Arrays.asList(imgData), toBinary, 1,dst)[0];
//    return proba(imgData, toBinary, 1)[0];
  }

  public static void writeToInput(byte[] imgData, FloatBuffer buf, boolean toBinary) {
    for (byte b : imgData) {
      if (toBinary) buf.put(b == 0 ? 0 : 1);
      else buf.put(b);
    }
  }

  public int predict(FloatBuffer inputBuf) {
    int[] predictions = new int[1];
    return predict(inputBuf, 1, predictions)[0];
  }

  public int[] predict(FloatBuffer inputBuf, int batchSize, int[] predictions) {
    float[][] probs = new float[batchSize][10];
    proba(inputBuf, batchSize, probs);
//    int[] predictions = new int[batchSize];
    for (int i = 0; i < batchSize; ++i) {
      predictions[i] = (int) findMaxPos(probs[i])[0];
    }
    return predictions;
  }

  public float[][] proba(Iterable<byte[]> imgBatch, boolean toBinary, int batchSize, float[][] dst) {
    FloatBuffer inputBuf = FloatBuffer.wrap(inputSpace, 0, batchSize * imgSize * imgSize);
    writeToInput(imgBatch, inputBuf.slice(), toBinary);
    return proba(inputBuf,batchSize,dst);
//    return predictions;
  }

  public float[][] proba(byte[][] imgBatch, boolean toBinary, int batchSize, float[][] dst) {
    FloatBuffer inputBuf = FloatBuffer.wrap(inputSpace, 0, batchSize * imgSize * imgSize);
    writeToInput(imgBatch, inputBuf.slice(), toBinary);
    return proba(inputBuf,batchSize,dst);
//    return predictions;
  }

  public float[][] proba(FloatBuffer inputBuf, int batchSize, float[][] probas) {
    Tensor inputTensor = Tensor.create(new long[] {batchSize, imgSize * imgSize}, inputBuf.slice());
    Tensor result =
            sess.runner()
                    .feed("input_tensor", inputTensor)
                    .feed("dropout/keep_prob", keepProb)
                    .fetch("output_tensor")
                    .run()
                    .get(0);
//    float[][] predictions = new float[batchSize][10];
    result.copyTo(probas);

//    Random rd=new Random();
//    for (int i=0;i<probas.length;++i){
//      for (int j=0;j<10;++j){
//        probas[i][j]= rd.nextFloat();
//      }
//    }
    inputTensor.close();
    result.close();
    return probas;
  }

  public static void showImgMat(byte[] imgData) {
    //    System.out.println();
    int c = 0;
    for (byte b : imgData) {
      ++c;
      System.out.format("%2d", b);
      if (c % 28 == 0) System.out.println();
    }
  }

  public static void showImgMat(FloatBuffer buf, int len) {
    //    System.out.println();
    int c = 0;
    for (int i = 0; i < len; ++i) {
      c++;
      System.out.format("%2d", (int) buf.get(i));
      if (c % 28 == 0) System.out.println();
    }
  }

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
    return new float[] {maxPos, max};
  }

  public void close() {
    sess.close();
  }

  public static void main(String[] args) {
    MnistReader mrTest =
        new MnistReader(
            "data/t10k-labels-idx1-ubyte.idx1-ubyte", "data/t10k-images-idx3-ubyte.idx3-ubyte");
    //            new MnistReader(
    //                    "data/train-labels-idx1-ubyte.idx1-ubyte",
    // "data/train-images-idx3-ubyte.idx3-ubyte");
    int testSize = 0, correctCt = 0;
    try (DigitRecognitor reco = new DigitRecognitor("mnist", "serve", 28)) {
      long startTime = System.currentTimeMillis();
      for (Object pair : mrTest) {
        ++testSize;
        Object[] pairLs = (Object[]) pair;
        byte[] imgData = (byte[]) pairLs[0];
        int label = (int) pairLs[1];
        long t1 = System.currentTimeMillis();
        float[][] dst=new float[1][10];
        int prediction = reco.predict(imgData, true);
        long t2 = System.currentTimeMillis();
        if (prediction == label) ++correctCt;
        //        else {
        //          System.out.format(
        //              "\nThis is %d, should be %d, time used = %d ms\n", prediction, label, t2 -
        // t1);
        //          showImgMat(reco.inputBuf, 28 * 28);
        //        }
        if (testSize % 1000 == 0) {
          System.out.format(
              "\nThis is %d, should be %d, time used = %d ms\n", prediction, label, t2 - t1);
          //          showImgMat(reco.inputBuf, 28 * 28);
        }
      }
      long endTime = System.currentTimeMillis();
      System.out.format(
          "Correct/Total =  %d/%d. Accuracy = %f, total time used = %d ms, avg time per img = %f ms\n",
          correctCt,
          testSize,
          ((float) correctCt / testSize),
          endTime - startTime,
          (double) (endTime - startTime) / testSize);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
