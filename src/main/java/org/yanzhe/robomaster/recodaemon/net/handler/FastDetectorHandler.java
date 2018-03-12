package org.yanzhe.robomaster.recodaemon.net.handler;

import com.google.protobuf.Int32Value;
import org.yanzhe.robomaster.recodaemon.core.DigitRecognizer;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells.Cell;

import java.util.List;

public class FastDetectorHandler extends DetectorHandler {
  protected final boolean toBinary;

  public FastDetectorHandler(DigitRecognizer recognitor, boolean sync, boolean toBinary) {
    super(recognitor, sync);
    this.toBinary = toBinary;
  }

  @Override
  protected Cell detect(List<Cell> cells) {
    for (Cell cell : cells) {
      int goal = cell.getGoal().getValue();
      byte[] imgData = cell.getImg().toByteArray();
//      int predict=1;
      int predict = recognitor.predict(imgData, toBinary);
      logger.debug("This is {}", predict);
//      DigitRecognizer.showImgMat(imgData);
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
