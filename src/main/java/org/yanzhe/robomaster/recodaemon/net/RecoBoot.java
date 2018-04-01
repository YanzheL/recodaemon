package org.yanzhe.robomaster.recodaemon.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.util.Version;
import io.netty.util.concurrent.Future;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.core.DetectorFactory;
import org.yanzhe.robomaster.recodaemon.core.classifier.CnnDigitClassifier;
import org.yanzhe.robomaster.recodaemon.core.classifier.ImageClassifier;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class RecoBoot {
  protected ImageClassifier recognitor;
  protected EventLoopGroup eventLoopGroup;
  protected static Logger logger = LogManager.getLogger(RecoBoot.class);

  public RecoBoot() {
      recognitor = DetectorFactory.provide(CnnDigitClassifier.class, "models/mnist_cnn");
  }

  public static void main(String[] args) {
    //
    // System.load("/home/trinity/IdeaProjects/recodaemon/src/main/java/resources/lib/tensorflow/libtensorflow_jni.so");
    //            System.loadLibrary("tensorflow_jni");
    //    System.out.println(System.mapLibraryName("tensorflow_jni"));
    logger.info("App booting...");
    logger.info("Netty versions:");
    Version.identify().forEach((k, v) -> logger.info("{} = {}", k, v));
    RecoBoot boot = new RecoBoot();
    try {
      boot.bootstrap();
      logger.info("Boot success");
    } catch (Exception e) {
      logger.error("Boot exception <{}>, message: {}", e, e.getMessage());
    }
  }

  public void bootstrap() {
    if (SystemUtils.IS_OS_MAC) {
      logger.info("Detected macOS System");
      bootBSD("/tmp/recodaemon.sock", 3316);
    } else if (SystemUtils.IS_OS_WINDOWS) {
      logger.info("Detected Windows System");
      bootGeneral(3316);
    } else if (SystemUtils.IS_OS_LINUX) {
      logger.info("Detected Linux System");
      bootLinux("/run/recodaemon.sock", 3316);
    }
    Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(true)));
  }

  private void bootBSD(String sockpath, int port) {
    logger.info("Now booting for BSD...");
    logger.info("Listening on port {}, Domain socks path = {}", port, sockpath);
    eventLoopGroup = new KQueueEventLoopGroup();
    boot(eventLoopGroup, KQueueServerSocketChannel.class, new InetSocketAddress(port));
    boot(eventLoopGroup, KQueueServerDomainSocketChannel.class, new DomainSocketAddress(sockpath));
  }

  private void bootLinux(String sockpath, int port) {
    logger.info("Now booting for Linux...");
    logger.info("Listening on port {}, Domain socks path = {}", port, sockpath);
    eventLoopGroup = new EpollEventLoopGroup();
    boot(eventLoopGroup, EpollServerSocketChannel.class, new InetSocketAddress(port));
    boot(eventLoopGroup, EpollServerDomainSocketChannel.class, new DomainSocketAddress(sockpath));
  }

  private void shutdown(boolean block) {
    Future future = eventLoopGroup.shutdownGracefully();
    logger.info("Now begin gracefully shutdown...");
    if (block)
      try {
        future.sync();
        recognitor.close();
        logger.info("Gracefully shutdown ok.");
        LogManager.shutdown();
        System.out.println("Gracefully shutdown ok, stdout");
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  private void bootGeneral(int port) {
    logger.info("Now booting for General...");
    logger.info("Listening on port {}", port);
    eventLoopGroup = new NioEventLoopGroup();
    boot(eventLoopGroup, NioServerSocketChannel.class, new InetSocketAddress(port));
  }

  private ChannelFuture boot(
          EventLoopGroup eventGroup,
          Class<? extends ServerChannel> serverChannelCls,
          SocketAddress addr) {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap
        .group(eventGroup)
        .channel(serverChannelCls)

            //        .childHandler(new ProtoBufInitializer(TargetCells.getDefaultInstance()))
            //            .childHandler(new DebugHandler());
        .childHandler(new RecoHandlersInitializer(recognitor));
    return bootstrap.bind(addr);
    //    future.sync();
  }
}
