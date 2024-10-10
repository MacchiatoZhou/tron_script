package myDiscover;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.tron.p2p.stats.TrafficStats;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MyConfig.init();
        MyConfig.test();
        MyEventHandler myEventHandler=new MyEventHandler(null);
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(TrafficStats.udp);
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new MyP2pPacketDecoder());
                            MyMessageHandler messageHandler = new MyMessageHandler(ch, myEventHandler);
                            myEventHandler.setMessageHandler(messageHandler);
                            ch.pipeline().addLast(messageHandler);
                        }
                    });

            // 启动客户端，绑定任意端口
            Channel channel = bootstrap.bind(MyConfig.getFromPort()).sync().channel();

            //test
            NodeIdTable nodeIdTable1 =new NodeIdTable();
            Byte[] byteId = Tool.toByteArray(MyConfig.getId());
            nodeIdTable1.init(new NodeId(byteId));
            //test

            // 构建自定义消息
            MyKadPingMessage PingMsg = new MyKadPingMessage();

            // 构建 UdpEvent
            InetSocketAddress targetAddress = new InetSocketAddress(MyConfig.getToIp(), MyConfig.getToPort());
            UdpEvent udpEvent = new UdpEvent(PingMsg, targetAddress);

            // 获取 MessageHandler 实例并发送消息
            MyMessageHandler myMessageHandler = (MyMessageHandler) channel.pipeline().last();
            startSendingMessages(channel,myMessageHandler,udpEvent);

            // 等待通道关闭
            channel.closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }
    public static void startSendingMessages(Channel channel, MyMessageHandler messageHandler, UdpEvent udpEvent) {
        new Thread(() -> {
            try {
                while (messageHandler.isKeepSending()) {
                    // 发送消息
                    //System.out.println("Sending message...");
                    messageHandler.accept(udpEvent);

                    // 设置发送间隔，避免过于频繁
                    Thread.sleep(1000); // 每隔1秒发送一次
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
