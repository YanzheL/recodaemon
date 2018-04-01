//package org.yanzhe.robomaster.recodaemon.net.handler;
//
//import com.google.protobuf.Int32Value;
//import com.google.protobuf.UInt32Value;
//import com.google.protobuf.UInt64Value;
//import org.yanzhe.robomaster.recodaemon.core.classifier.ImageClassifier;
//import org.yanzhe.robomaster.recodaemon.core.processor.ImageProcessor;
//import org.yanzhe.robomaster.recodaemon.core.utils.CoreUtils;
//import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells.Cell;
//
//import java.util.List;
//
//public class BatchDetectorHandler extends DetectorHandler {
//
//  public BatchDetectorHandler(ImageClassifier recognitor, ImageProcessor processor, boolean sync) {
//    super(recognitor, processor, sync);
//  }
//
//  @Override
//  protected Cell detect(List<Cell> cells) {
//    int bestPos = -1;
//    int batchSize = cells.size();
//    byte[][] imgBatch = new byte[batchSize][];
//    if (batchSize == 0) return Cell.getDefaultInstance();
//    Cell firstCell = cells.get(0);
//    int goal = firstCell.getGoal().getValue();
//    long seq = firstCell.getSeq().getValue();
//    for (Cell cell : cells) {
//      int pos = cell.getPos().getValue();
//      imgBatch[pos] = cell.getImg().toByteArray();
//    }
//    float[][] probas = new float[batchSize][10];
//
//    classifier.proba(imgBatch, batchSize, probas);
//    int i = 0;
//    float bestProba = 0;
//    for (float[] prob : probas) {
//      float[] oneBest = CoreUtils.findMaxPos(prob);
//      if ((int) oneBest[0] == goal) {
//        if (oneBest[1] > bestProba) {
//          bestPos = i;
//          bestProba = oneBest[1];
//        }
//      }
//      ++i;
//    }
////    if (bestPos >= 0) CnnDigitClassifier.showImgMat(imgBatch[bestPos]);
//    logger.info("bestPos = {}", bestPos);
//
//    return Cell.newBuilder()
//        .setGoal(UInt32Value.newBuilder().setValue(goal).build())
//        .setPos(Int32Value.newBuilder().setValue(bestPos).build())
//        .setSeq(UInt64Value.newBuilder().setValue(seq).build())
//        .build();
//  }
//}
