package org.yanzhe.robomaster.recodaemon.net.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@Sharable
public class DebugHandler extends ChannelOutboundHandlerAdapter {
//    @Override
//    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
//        System.out.println("ddddddddddddddewterwtewrtewrterw");
////        ctx.writeAndFlush(msg);
//        TargetCellsProto.TargetCells cells=(TargetCellsProto.TargetCells)msg;
//        System.out.println(cells.getCellsList());
//
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("active");
//        ctx.fireChannelActive();
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("Complete");
////        ctx.writeAndFlush("hhh5467777777777777777777777777777777777").addListener(ChannelFutureListener.CLOSE);
//        ctx.fireChannelReadComplete();
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.format("Msg");
        super.write(ctx, msg, promise);
    }
}
