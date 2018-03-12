package org.yanzhe.robomaster.recodaemon.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.core.DigitRecognizer;
import org.yanzhe.robomaster.recodaemon.net.handler.FastDetectorHandler;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto;

public class RecoHandlersInitializer extends ChannelInitializer {
  private DigitRecognizer recognitor;
  protected static Logger logger = LogManager.getLogger(RecoHandlersInitializer.class);

  public RecoHandlersInitializer(String modelDir) {
    recognitor = new DigitRecognizer(modelDir);
  }

  public RecoHandlersInitializer(DigitRecognizer recognitor) {
    this.recognitor = recognitor;
  }

  @Override
  protected void initChannel(Channel ch) {
    ChannelPipeline pipline = ch.pipeline();
    pipline.addLast(new ProtoBufInitializer(TargetCellsProto.TargetCells.getDefaultInstance()));
    pipline.addLast(new FastDetectorHandler(recognitor, false, false));
//    pipline.addLast(new DefaultEventExecutorGroup(8), new BatchDetectorHandler(recognitor, false, false));
//    pipline.addLast(new EnqueHandler());
    logger.debug("Channel initialized");
  }
}
