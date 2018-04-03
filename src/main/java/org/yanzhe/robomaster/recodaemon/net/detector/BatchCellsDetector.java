package org.yanzhe.robomaster.recodaemon.net.detector;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.core.classifier.AbstractImageClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.ImageProcessor;
import org.yanzhe.robomaster.recodaemon.core.utils.CachedSingleton;
import org.yanzhe.robomaster.recodaemon.core.utils.CoreUtils;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.Cell;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells;

import java.util.List;

public class BatchCellsDetector<C extends AbstractImageClassifier, P extends ImageProcessor>
        implements Detector {

    protected static Logger logger = LogManager.getLogger(FastCellsDetector.class);
    protected C classifier;
    protected P processor;

    public BatchCellsDetector(Class<C> classifierCls, Class<P> processorCls) {
        this.classifier = CachedSingleton.getInstance(classifierCls);
        this.processor = CachedSingleton.getInstance(processorCls);
    }

    @Override
    public Any detect(Any body) throws Exception {
        Any result;
        //    try {
        TargetCells targetCells = body.unpack(TargetCells.class);
        List<Cell> cells = targetCells.getCellsList();

        long t1 = System.currentTimeMillis();
        Cell resultCell = _detect(cells);
        long t2 = System.currentTimeMillis();
        logger.debug("Batch size = {}, Time used = {} ms\n", cells.size(), t2 - t1);
        result = Any.pack(resultCell);

        return result;
    }

    protected Cell _detect(List<Cell> cells) {
        int bestPos = -1;
        int batchSize = cells.size();
        byte[][] imgBatch = new byte[batchSize][];
        if (batchSize == 0) return Cell.getDefaultInstance();
        Cell firstCell = cells.get(0);
        int goal = firstCell.getGoal().getValue();
        long seq = firstCell.getSeq().getValue();
        for (Cell cell : cells) {
            int pos = cell.getPos().getValue();
            imgBatch[pos] = cell.getImg().toByteArray();
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
                .build();
    }
}

