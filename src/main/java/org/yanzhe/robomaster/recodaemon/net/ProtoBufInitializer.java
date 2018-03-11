package org.yanzhe.robomaster.recodaemon.net;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.net.handler.DebugHandler;

public class ProtoBufInitializer extends ChannelInitializer<Channel> {
  private final MessageLite lite;
  protected static Logger logger = LogManager.getLogger(ProtoBufInitializer.class);

  public ProtoBufInitializer(MessageLite lite) {
    this.lite = lite;
    logger.debug("Instance Created");
  }

  @Override
  protected void initChannel(Channel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new ProtobufVarint32FrameDecoder());
    pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
    pipeline.addLast(new ProtobufEncoder());
    pipeline.addLast(new ProtobufDecoder(lite));
    logger.debug("Channel initialized");
  }
}
