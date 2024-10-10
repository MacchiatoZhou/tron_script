package myConnection.socket;

import myDiscover.MyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;


public class MyPeerServer  {
    private ChannelFuture channelFuture;
    private boolean listening;

    public  void init(){
        int port = MyConfig.getFromPort();
        if (port >0){
            new Thread(() -> start(port),"PeerServer").start();
        }
    }
    public void close(){
        if (listening && channelFuture != null && channelFuture.channel().isOpen()) {
            try {
                System.out.println("Closing TCP server...");
                channelFuture.channel().close().sync();
            } catch (Exception e) {
                System.out.println("Closing TCP server failed."+e);
            }
        }
    }
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1,
                new BasicThreadFactory.Builder().namingPattern("peerBoss").build());
        //if threads = 0, it is number of core * 2
        EventLoopGroup workerGroup = new NioEventLoopGroup(0,
                new BasicThreadFactory.Builder().namingPattern("peerWorker-%d").build());
        MyP2pChannelInitializer p2pChannelInitializer = new MyP2pChannelInitializer("", false, true);
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);

            b.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, MyConfig.NODE_CONNECTION_TIMEOUT);

            b.handler(new LoggingHandler());
            b.childHandler(p2pChannelInitializer);

            // Start the client.
            System.out.println("TCP listener started, bind port {}"+port);

            channelFuture = b.bind(port).sync();

            listening = true;

            // Wait until the connection is closed.
            channelFuture.channel().closeFuture().sync();

            System.out.println("TCP listener closed");

        } catch (Exception e) {
            System.out.printf("Start TCP server failed"+ e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            listening = false;
        }
    }
}
