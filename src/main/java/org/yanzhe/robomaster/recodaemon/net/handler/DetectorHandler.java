package org.yanzhe.robomaster.recodaemon.net.handler;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.core.classifier.CnnDigitClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.DefaultImageProcessor;
import org.yanzhe.robomaster.recodaemon.core.processor.FirePurifyProcessor;
import org.yanzhe.robomaster.recodaemon.net.detector.BatchCellsDetector;
import org.yanzhe.robomaster.recodaemon.net.detector.Detector;
import org.yanzhe.robomaster.recodaemon.net.detector.FastCellsDetector;
import org.yanzhe.robomaster.recodaemon.net.detector.LedDetector;
import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto.RecoMethod;
import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto.RpcRequest;
import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto.RpcResponse;
import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto.Status;

// @ChannelHandler.Sharable
public class DetectorHandler extends SimpleChannelInboundHandler<RpcRequest> {
  protected boolean sync;
  protected static Logger logger = LogManager.getLogger(DetectorHandler.class);
  protected RecoMethod lastMethod = RecoMethod.RECO_SIMPLE_HW_DIGIT;
  protected Detector detector;
  protected static DefaultEventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(12);
  protected int callerId;

  public DetectorHandler(boolean sync) {
    this.sync = sync;
  }

  protected static RpcResponse getErrorResponse(int callerId, Throwable e) {
    return composeResponse(callerId, getErrorCode(e), Any.getDefaultInstance());
  }

  protected static Status getErrorCode(Throwable cause) {
    if (cause instanceof InvalidProtocolBufferException)
      return Status.BAD_REQUEST_PROTO;
    else if (cause instanceof NoSuchMethodException)
      return Status.METHOD_NOT_FOUND;
    else if (cause instanceof NullPointerException)
      return Status.RPC_ERROR;
    else
      return Status.UNRECOGNIZED;
  }

  protected static RpcResponse composeResponse(int callerId, Status status, Any body) {
    return RpcResponse.newBuilder()
            .setCallerId(callerId)
            .setStatus(status)
            .setData(body)
            .build();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
    //        logger.debug("Channel Read");
    callerId = rpcRequest.getCallerId();
    RecoMethod method = rpcRequest.getMethod();
//    logger.debug("Method = {}",method);
    if (detector == null || method != lastMethod) {
      switch (method) {
        case RECO_FIRE_HW_DIGIT:
            detector = new FastCellsDetector(CnnDigitClassifier.class, FirePurifyProcessor.class);
          break;
        case RECO_SIMPLE_HW_DIGIT:
          detector = new BatchCellsDetector(CnnDigitClassifier.class, DefaultImageProcessor.class);
          break;
        case RECO_LED_DIGIT:
          detector = new LedDetector();
          break;
        default:
          throw new NoSuchMethodException("No such reco method");
      }
      lastMethod = method;
    }

    Any body = rpcRequest.getData();

    if (sync) {
      Any result = detector.detect(body);
      channelHandlerContext.writeAndFlush(composeResponse(callerId, Status.SUCCESS, result));
    } else {
      Future backrun = eventExecutors.submit(
              () -> {
                try {
                  long t1 = System.currentTimeMillis();
                  Any resultCell = detector.detect(body);
                  long t2 = System.currentTimeMillis();
                  channelHandlerContext.writeAndFlush(composeResponse(callerId, Status.SUCCESS, resultCell));
                } catch (Exception e) {
                  channelHandlerContext.writeAndFlush(getErrorResponse(callerId, e));
                }

              });
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//    logger.error("Caught exception ");
    ctx.writeAndFlush(getErrorResponse(callerId, cause));
  }
}
