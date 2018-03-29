package org.yanzhe.robomaster.recodaemon.net.handler;

import com.google.protobuf.Int32Value;
import org.yanzhe.robomaster.recodaemon.core.ImageClassifier;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells.Cell;

import java.util.List;

public class FastDetectorHandler extends DetectorHandler {

  public FastDetectorHandler(ImageClassifier recognitor, boolean sync) {
    super(recognitor, sync);
  }

  @Override
  protected Cell detect(List<Cell> cells) {
    for (Cell cell : cells) {
      int goal = cell.getGoal().getValue();
      byte[] imgData = cell.getImg().toByteArray();
//      CoreUtils.toUcharBytes(imgData);
//      int predict=1;
      int predict = recognitor.predict(imgData);
      logger.debug("This is {}", predict);
//      CoreUtils.showImgMat(imgData);
      if (goal == predict) {
        return cell.toBuilder().clearImg().build();
      }
    }
    return cells
        .get(0)
        .toBuilder()
        .clearImg()
        .setPos(Int32Value.newBuilder().setValue(-1).build())
        .build();
  }
}
