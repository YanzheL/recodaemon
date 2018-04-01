package org.yanzhe.robomaster.recodaemon.net;

import com.google.protobuf.Int32Value;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import org.testng.annotations.DataProvider;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.Cell;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells;
import org.yanzhe.robomaster.recodaemon.utils.MnistReader;

import java.util.Iterator;

public class TargetCellsProvider implements Iterator<Object[]>, Iterable<Object[]> {

  private MnistReader reader;
  private int batchSize;
  private int count;

  public TargetCellsProvider() {
    reader =
        //        new MnistReader(
        //            "data/t10k-labels-idx1-ubyte.idx1-ubyte",
        // "data/t10k-images-idx3-ubyte.idx3-ubyte");

        new MnistReader(
            "data/train-labels-idx1-ubyte.idx1-ubyte", "data/train-images-idx3-ubyte.idx3-ubyte");
    batchSize = 9;
  }

  @Override
  public Iterator<Object[]> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    return count < 100000 && reader.hasNext();
  }

  @Override
  public Object[] next() {
    ++count;
    try {
      Thread.sleep(33);
    } catch (Exception e) {

    }
    int ct = 0;
    //      Object[] batch=new Object[batchSize];
    TargetCells.Builder batchBuilder = TargetCells.newBuilder();
    for (Object[] piece : reader) {
      byte[] imgData = (byte[]) piece[0];
      int label = (int) piece[1];
        Cell cell =
                Cell.newBuilder()
              .setPos(Int32Value.newBuilder().setValue(ct).build())
              .setGoal(UInt32Value.newBuilder().setValue(7).build())
//              .setImg(ByteString.copyFrom(imgData))
              .setSeq(UInt64Value.newBuilder().setValue(count).build())
              .build();
      batchBuilder.addCells(cell);
      ++ct;
      if (ct == batchSize) {
        break;
      }
    }
    return new Object[] {batchBuilder.build()};
  }

  @DataProvider(name = "targetCells")
  public Iterator<Object[]> provide() {
    return this;
  }
}
