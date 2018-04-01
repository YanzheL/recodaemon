package org.yanzhe.robomaster.recodaemon.net.detector;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.yanzhe.robomaster.recodaemon.core.classifier.AbstractImageClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.ImageProcessor;
import org.yanzhe.robomaster.recodaemon.core.utils.CachedSingleton;
import org.yanzhe.robomaster.recodaemon.core.utils.CoreUtils;
import org.yanzhe.robomaster.recodaemon.net.proto.ImageProto.Image;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.Cell;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells;

import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.resize;

public class FastCellsDetector<C extends AbstractImageClassifier, P extends ImageProcessor>
        implements Detector {

  protected static Logger logger = LogManager.getLogger(FastCellsDetector.class);
  protected C classifier;
  protected P processor;

  public FastCellsDetector(Class<C> classifierCls, Class<P> processorCls) {
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
//    logger.debug(resultCell);
    long t2 = System.currentTimeMillis();
    logger.debug("Batch size = {}, Time used = {} ms\n", cells.size(), t2 - t1);
    result = Any.pack(resultCell);
//    logger.debug(result);

    //    } catch (InvalidProtocolBufferException e) {
    //      logger.error(
    //          "Unpack body to type <TargetCells> failed, exception <{}>, msg '{}'",
    //          e.toString(),
    //          e.getMessage());
    //    }

    return result;
  }

  protected Cell _detect(List<Cell> cells) {
    for (Cell cell : cells) {
      int goal = cell.getGoal().getValue();
      Image imgProto = cell.getImg();
      Mat img = CoreUtils.toMat(imgProto);
//            imshow("a",img);
//            waitKey(0);
      Mat pureImg = processor.process(img);
//      imshow("a",pureImg);
//            waitKey(0);
      int size = classifier.acceptSize();
      resize(pureImg, pureImg, new Size(size, size));
//      threshold(pureImg,pureImg,127,1,THRESH_BINARY_INV);
      byte[] pureData = new byte[pureImg.rows() * pureImg.cols() * pureImg.channels()];
      pureImg.data().get(pureData);
      int predict = classifier.predict(pureData);
      logger.debug("This is {}", predict);
//      CoreUtils.showImgMat(pureData);
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
