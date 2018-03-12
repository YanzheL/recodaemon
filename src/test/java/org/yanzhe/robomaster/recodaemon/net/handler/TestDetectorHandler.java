package org.yanzhe.robomaster.recodaemon.net.handler;

import io.netty.channel.embedded.EmbeddedChannel;
import org.testng.annotations.Test;
import org.yanzhe.robomaster.recodaemon.core.DigitRecognizer;
import org.yanzhe.robomaster.recodaemon.net.RecoHandlersInitializer;
import org.yanzhe.robomaster.recodaemon.net.TargetCellsProvider;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells.Cell;

@Test(singleThreaded = true)
public class TestDetectorHandler {
  private DetectorHandler dh;
    private DigitRecognizer recognitor;
  EmbeddedChannel channel;

  TestDetectorHandler() {
      recognitor = new DigitRecognizer("models/mnist");
    //    channel = new EmbeddedChannel(new BatchDetectorHandler(recognitor, true));
//    channel = new EmbeddedChannel(new BatchDetectorHandler(recognitor, true));
    channel = new EmbeddedChannel(new DebugHandler(),new RecoHandlersInitializer(recognitor));
  }

  @Test(dataProvider = "targetCells", dataProviderClass = TargetCellsProvider.class)
  public void testFastDetectorHandler(TargetCells msg) {
    channel.writeInbound(msg);
    //    Assert.assertTrue(channel.writeInbound(msg));
    //    System.out.println(msg);
    //    System.out.format("ReadInBound=%s\n", channel.readInbound());
    //    System.out.println(channel.readOutbound());
    Object ret = channel.readOutbound();
    if (ret != null) {
      Cell retc=(Cell)ret;
//      System.out.format("Seq = %d, Goal = %d, Pos = %d\n",retc.getSeq().getValue(),retc.getGoal().getValue(),retc.getPos().getValue());
//      System.out.println(ret);
      //      System.out.println(new String(((UnpooledHeapByteBuf) ret).array(), "ascii"));
    }
    //    Assert.assertNotNull(ret);
    //    System.out.format("ReadOutBound=%s\n", channel.readOutbound().toString());
  }
}
