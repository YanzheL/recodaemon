package org.yanzhe.robomaster.recodaemon.net.detector;

import com.google.protobuf.Int32Value;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.yanzhe.robomaster.recodaemon.core.classifier.AbstractImageClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.ImageProcessor;
import org.yanzhe.robomaster.recodaemon.core.utils.CachedSingleton;
import org.yanzhe.robomaster.recodaemon.core.utils.CoreUtils;
import org.yanzhe.robomaster.recodaemon.net.proto.ImageProto;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.Cell;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.resize;

public class BatchCellsDetector<C extends AbstractImageClassifier, P extends ImageProcessor>
        extends AbstractCellsDetector {

    protected static Logger logger = LogManager.getLogger(BatchCellsDetector.class);
    protected C classifier;
    protected P processor;

    public BatchCellsDetector(Class<C> classifierCls, Class<P> processorCls) {
        this.classifier = CachedSingleton.getInstance(classifierCls);
        this.processor = CachedSingleton.getInstance(processorCls);
    }


    @Override
    protected Cell _detect(List<Cell> cells) {
        int size = classifier.acceptSize();
        int bestPos = -1;
        int batchSize = cells.size();
//        byte[][] imgBatch = new byte[batchSize][];
        List<byte[]> imgBatch = new ArrayList<>();
        if (batchSize == 0) return Cell.getDefaultInstance();
        Cell firstCell = cells.get(0);
        int goal = firstCell.getGoal().getValue();
        long seq = firstCell.getSeq().getValue();
        for (Cell cell : cells) {
            int pos = cell.getPos().getValue();
            Mat img = CoreUtils.toMat(cell.getImg());
//            CoreUtils.showImgMat(img);
            Mat pureImg = processor.process(img);
            resize(pureImg, pureImg, new Size(size, size));
            byte[] pureData = new byte[pureImg.rows() * pureImg.cols() * pureImg.channels()];
            pureImg.data().get(pureData);
            imgBatch.add(pureData);
//            imgBatch[pos] = pureData;
        }
        float[][] probas = new float[batchSize][10];

        classifier.proba(imgBatch, batchSize, probas);
        int i = 0;
        float bestProba = 0;
        for (float[] prob : probas) {
            float[] oneBest = CoreUtils.findMaxPos(prob);
            if ((int) oneBest[0] == goal) {
                if (oneBest[1] > bestProba) {
                    bestPos = i;
                    bestProba = oneBest[1];
                }
            }
            ++i;
        }
//    if (bestPos >= 0) CnnDigitClassifier.showImgMat(imgBatch[bestPos]);
        logger.info("bestPos = {}", bestPos);

        return Cell.newBuilder()
                .setGoal(UInt32Value.newBuilder().setValue(goal).build())
                .setPos(Int32Value.newBuilder().setValue(bestPos).build())
                .setSeq(UInt64Value.newBuilder().setValue(seq).build())
                .setImg(ImageProto.Image.getDefaultInstance())
                .build();
    }
}

