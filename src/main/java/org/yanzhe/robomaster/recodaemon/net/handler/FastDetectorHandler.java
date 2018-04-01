package org.yanzhe.robomaster.recodaemon.net.handler;

import com.google.protobuf.Int32Value;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.yanzhe.robomaster.recodaemon.core.classifier.ImageClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.ImageProcessor;
import org.yanzhe.robomaster.recodaemon.net.proto.ImageProto.Image;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells.Cell;

import java.util.List;

import static org.bytedeco.javacpp.opencv_core.CV_8UC;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

public class FastDetectorHandler extends DetectorHandler {

  public FastDetectorHandler(ImageClassifier recognitor, ImageProcessor processor, boolean sync) {
    super(recognitor, processor, sync);
  }

  @Override
  protected Cell detect(List<Cell> cells) {
    for (Cell cell : cells) {
      int goal = cell.getGoal().getValue();
      Image imgProto = cell.getImg();
      int rows = imgProto.getRows().getValue();
      int cols = imgProto.getCols().getValue();
      int channels = imgProto.getChannels().getValue();
      byte[] imgData = imgProto.getData().toByteArray();
      Mat img = new Mat(rows, cols, CV_8UC(channels), new BytePointer(imgData));
//      imshow("a",img);
//      waitKey(0);
      Mat pureImg = processor.process(img);
      int size = classifier.acceptSize();
      resize(pureImg, pureImg, new Size(size, size));
      byte[] pureData = new byte[pureImg.rows() * pureImg.cols() * pureImg.channels()];
      pureImg.data().get(pureData);
      int predict = classifier.predict(pureData);
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
