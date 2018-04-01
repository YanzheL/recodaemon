package org.yanzhe.robomaster.recodaemon.net.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.core.classifier.ImageClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.ImageProcessor;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells.Cell;

import java.util.List;

// @ChannelHandler.Sharable
public abstract class DetectorHandler extends SimpleChannelInboundHandler<TargetCells> {
    protected static ImageClassifier classifier;
    protected static ImageProcessor processor;
  protected static DefaultEventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(12);
  protected boolean sync;
  protected static Logger logger = LogManager.getLogger(DetectorHandler.class);

    public DetectorHandler(ImageClassifier classifier, ImageProcessor processor, boolean sync) {
        DetectorHandler.classifier = classifier;
        DetectorHandler.processor = processor;
    this.sync = sync;
    //        logger.debug("Instance created");
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, TargetCells targetCells) {
    //        logger.debug("Channel Read");
    if (sync) syncRead(ctx, targetCells);
    else asyncRead(ctx, targetCells);
    //    System.gc();
  }

  private void syncRead(ChannelHandlerContext ctx, TargetCells targetCells) {

    List<Cell> cells = targetCells.getCellsList();
    long t1 = System.currentTimeMillis();
    Cell resultCell = detect(cells);
    //    Cell resultCell=cells.get(0);
    long t2 = System.currentTimeMillis();

    logger.debug("Batch size = {}, Time used = {} ms\n", cells.size(), t2 - t1);
    ChannelFuture future = ctx.writeAndFlush(resultCell);
    //    future.addListener(ChannelFutureListener.CLOSE);
    //    ctx.fireChannelReadComplete();
  }

  private void asyncRead(ChannelHandlerContext ctx, TargetCells targetCells) {
    //    System.out.println(targetCells);
    List<Cell> cells = targetCells.getCellsList();
    eventExecutors.submit(
        () -> {
          long t1 = System.currentTimeMillis();
          Cell resultCell = detect(cells);
          ctx.writeAndFlush(resultCell);
          long t2 = System.currentTimeMillis();
          logger.debug("Batch size = {}, Time used = {} ms\n", cells.size(), t2 - t1);
          //          logger.debug("Seq = {}, Goal = {}, Pos =
          // {}",resultCell.getSeq().getValue(),resultCell.getGoal().getValue(),resultCell.getPos().getValue());
        });
  }

  protected abstract Cell detect(List<Cell> cells);
}
