package org.yanzhe.robomaster.recodaemon.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.net.handler.DetectorHandler;
import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto;

public class RecoHandlersInitializer extends ChannelInitializer {
  protected static Logger logger = LogManager.getLogger(RecoHandlersInitializer.class);

  @Override
  protected void initChannel(Channel ch) {
    ChannelPipeline pipline = ch.pipeline();
      pipline.addLast(new ProtoBufInitializer(RpcMessageProto.RpcRequest.getDefaultInstance()));
      pipline.addLast(new DetectorHandler(false));
//    pipline.addLast(new DefaultEventExecutorGroup(8), new BatchDetectorHandler(classifier, false));
//    pipline.addLast(new EnqueHandler());
    logger.debug("Channel initialized");
  }
}
