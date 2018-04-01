package org.yanzhe.robomaster.recodaemon.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.core.DetectorFactory;
import org.yanzhe.robomaster.recodaemon.core.classifier.CnnDigitClassifier;
import org.yanzhe.robomaster.recodaemon.core.classifier.ImageClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.FirePurifyProcessor;
import org.yanzhe.robomaster.recodaemon.net.handler.FastDetectorHandler;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto;

public class RecoHandlersInitializer extends ChannelInitializer {
  private ImageClassifier recognitor;
  protected static Logger logger = LogManager.getLogger(RecoHandlersInitializer.class);

  public RecoHandlersInitializer(String modelDir) {
      recognitor = DetectorFactory.provide(CnnDigitClassifier.class, modelDir);
  }

  public RecoHandlersInitializer(ImageClassifier recognitor) {
    this.recognitor = recognitor;
  }

  @Override
  protected void initChannel(Channel ch) {
    ChannelPipeline pipline = ch.pipeline();
    pipline.addLast(new ProtoBufInitializer(TargetCellsProto.TargetCells.getDefaultInstance()));
      pipline.addLast(new FastDetectorHandler(recognitor, new FirePurifyProcessor(), true));
//    pipline.addLast(new DefaultEventExecutorGroup(8), new BatchDetectorHandler(classifier, false));
//    pipline.addLast(new EnqueHandler());
    logger.debug("Channel initialized");
  }
}
