package org.yanzhe.robomaster.recodaemon.core.classifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.yanzhe.robomaster.recodaemon.utils.MnistReader;

import java.nio.FloatBuffer;

import static org.yanzhe.robomaster.recodaemon.core.utils.CoreUtils.writeToInputBuf;

public class CnnDigitClassifier extends AbstractImageClassifier {
    private static Logger logger = LogManager.getLogger(CnnDigitClassifier.class);
    private final int imgSize = 28;
    private Session sess;
    private SavedModelBundle model;
    private float[] inputSpace;

    public CnnDigitClassifier() {
        this("models/mnist_cnn");
    }

    public CnnDigitClassifier(String modelDir) {
        this(modelDir, "serve");
    }

    public CnnDigitClassifier(String modelDir, String tags) {
        logger.info("Tensorflow version = {}", TensorFlow.version());
        this.model = SavedModelBundle.load(modelDir, tags);
//    Iterator<Operation> ops=model.graph().operations();
//    while (ops.hasNext()){
//      System.out.println(ops.next());
//    }
        this.sess = this.model.session();
        inputSpace = new float[9 * imgSize * imgSize];
    }


    //  private int predict(FloatBuffer inputBuf) {
    //    int[] predictions = new int[1];
    //    return predict(inputBuf, 1, predictions)[0];
    //  }

    //  private int[] predict(FloatBuffer inputBuf, int batchSize, int[] predictions) {
    //    float[][] probs = new float[batchSize][10];
    //    proba(inputBuf, batchSize, probs);
    //    for (int i = 0; i < batchSize; ++i) {
    //      predictions[i] = (int) CoreUtils.findMaxPos(probs[i])[0];
    //    }
    //    return predictions;
    //  }

    @Override
    public float[][] proba(
            Iterable<byte[]> imgBatch, int batchSize, float[][] dst) {
        FloatBuffer inputBuf = FloatBuffer.wrap(inputSpace, 0, batchSize * imgSize * imgSize);
        writeToInputBuf(imgBatch, inputBuf.slice());
        return proba(inputBuf, batchSize, dst);
    }

    private float[][] proba(FloatBuffer inputBuf, int batchSize, float[][] probas) {
        //    try (
        Tensor inputTensor = Tensor.create(new long[]{batchSize, imgSize, imgSize}, inputBuf.slice());
        Tensor result =
                sess.runner()
                        .feed("Placeholder:0", inputTensor)
                        //                    .feed("dropout/keep_prob", keepProb)
                        .fetch("Softmax:0")
                        .run()
                        .get(0);
        //    )
        //    {
        //    float[][] predictions = new float[batchSize][10];
        result.copyTo(probas);
        //    } catch (Exception e) {
        //      logger.error("Recognizer exception <{}>", e.getMessage());
        //    }

        //    Random rd=new Random();
        //    for (int i=0;i<probas.length;++i){
        //      for (int j=0;j<10;++j){
        //        probas[i][j]= rd.nextFloat();
        //      }
        //    }

        result.close();
        inputTensor.close();
        return probas;
    }

    @Override
    public int acceptSize() {
        return imgSize;
    }

    @Override
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
        try (CnnDigitClassifier reco = new CnnDigitClassifier("mnist", "serve")) {
            long startTime = System.currentTimeMillis();
            for (Object pair : mrTest) {
                ++testSize;
                Object[] pairLs = (Object[]) pair;
                byte[] imgData = (byte[]) pairLs[0];
                int label = (int) pairLs[1];
                long t1 = System.currentTimeMillis();
                float[][] dst = new float[1][10];
                int prediction = reco.predict(imgData);
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
