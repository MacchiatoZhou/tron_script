package myConnection;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import myConnection.handshake.MyHandshakeService;
import myConnection.keepalive.MyKeepAliveService;
import myConnection.message.MyMessage;
import myConnection.message.MyP2pDisconnectMessage;
import myConnection.socket.MyPeerClient;
import myConnection.socket.MyPeerServer;
import myDiscover.MyConfig;
import myDiscover.Tool;
import org.tron.p2p.connection.business.handshake.DisconnectCode;
import org.tron.p2p.discover.Node;
import org.tron.p2p.exception.P2pException;
import org.tron.p2p.protos.Connect;
import org.tron.p2p.utils.ByteArray;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MyChannelManager {

    @Getter
    private static MyHandshakeService handshakeService;
    @Getter
    private static MyPeerClient peerClient;

    private static MyPeerServer peerServer;

    private static MyKeepAliveService keepAliveService;

    private static MyNodeDetectService nodeDetectService;

    @Getter
    private static final Map<InetSocketAddress, MyChannel> channels = new ConcurrentHashMap<>();

    private static boolean isInit = false;

    public static volatile boolean isShutdown = false;


    public static void main(String[] args) {
        MyChannelManager.init();
        String localIdString;
        String remoteIp;
        byte[] randomId = Tool.generateRandomNodeId();
        MyConfig.init(randomId);
        for (int i = 0; i < args.length; i++) {
            if ("--localId".equals(args[i])) {
                if (i + 1 < args.length) {  // 确保端口值存在
                    try {
                        localIdString = args[i + 1];
                        MyConfig.init(localIdString);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid id: " + args[i + 1]);
                        System.exit(1);
                    }
                }
            }
            if("--remoteIp".equals(args[i])) {
                if (i + 1 < args.length) {  // 确保端口值存在
                    try {
                        remoteIp = args[i + 1];
                        MyConfig.setToIp(remoteIp);

                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ip: " + args[i + 1]);
                        System.exit(1);
                    }
                }
            }
        }

        ChannelFuture channelFuture = peerClient.connectAsync(MyConfig.getTo(), false);
//        if (channelFuture != null) {
//            // 监控 Channel 的关闭事件
//
//        }
    }

    public static void init(){
        isInit=true;
        handshakeService=new MyHandshakeService();
        peerClient = new MyPeerClient();
        peerServer = new MyPeerServer();
        keepAliveService = new MyKeepAliveService();
        //peerServer.init();
        peerClient.init();
        keepAliveService.init();

    }

    public static void connect(InetSocketAddress address) {
        peerClient.connect(address.getAddress().getHostAddress(), address.getPort(),
                ByteArray.toHexString(MyConfig.getLocalId()));
    }

    public static ChannelFuture connect(Node node, ChannelFutureListener future) {
        return peerClient.connect(node, future);
    }
    public static void notifyDisconnect(MyChannel channel) {
        if (channel.getInetSocketAddress() == null) {
            //log.warn("Notify Disconnect peer has no address.");
            return;
        }
        channels.remove(channel.getInetSocketAddress());
        MyConfig.handlerList.forEach(h -> h.onDisconnect(channel));
        InetAddress inetAddress = channel.getInetAddress();
        //if (inetAddress != null) {
            //banNode(inetAddress, Parameter.DEFAULT_BAN_TIME);
        //}
    }
    public static synchronized DisconnectCode processPeer(MyChannel channel) {

//        if (!channel.isActive() && !channel.isTrustPeer()) {
//            InetAddress inetAddress = channel.getInetAddress();
//            if (bannedNodes.getIfPresent(inetAddress) != null
//                    && bannedNodes.getIfPresent(inetAddress) > System.currentTimeMillis()) {
//                log.info("Peer {} recently disconnected", channel);
//                return DisconnectCode.TIME_BANNED;
//            }
//
//            if (channels.size() >= Parameter.p2pConfig.getMaxConnections()) {
//                log.info("Too many peers, disconnected with {}", channel);
//                return DisconnectCode.TOO_MANY_PEERS;
//            }
//
//            int num = getConnectionNum(channel.getInetAddress());
//            if (num >= Parameter.p2pConfig.getMaxConnectionsWithSameIp()) {
//                log.info("Max connection with same ip {}", channel);
//                return DisconnectCode.MAX_CONNECTION_WITH_SAME_IP;
//            }
//        }

//        if (StringUtils.isNotEmpty(channel.getNodeId())) {
//            for (Channel c : channels.values()) {
//                if (channel.getNodeId().equals(c.getNodeId())) {
//                    if (c.getStartTime() > channel.getStartTime()) {
//                        //c.close();
//                    } else {
//                        //log.info("Duplicate peer {}, exist peer {}", channel, c);
//                        return DisconnectCode.DUPLICATE_PEER;
//                    }
//                }
//            }
//        }

        channels.put(channel.getInetSocketAddress(), channel);

        //log.info("Add peer {}, total channels: {}", channel.getInetSocketAddress(), channels.size());
        return DisconnectCode.NORMAL;
    }
    public static void close() {
        if (!isInit || isShutdown) {
            return;
        }
        isShutdown = true;
        //connPoolService.close();
        keepAliveService.close();
        peerServer.close();
        peerClient.close();
        //nodeDetectService.close();
    }
    public static void processMessage(MyChannel channel, byte[] data) throws P2pException {
        if (data == null || data.length == 0) {
            throw new P2pException(P2pException.TypeEnum.EMPTY_MESSAGE, "");
        }
        if (data[0] >= 0) {
            handMessage(channel, data);
            return;
        }

        MyMessage message = MyMessage.parse(data);

        System.out.println("receive msg from channel "+channel.getInetSocketAddress()+"type: "+message);
//        if (message.needToLog()) {
//            //log.info("Receive message from channel: {}, {}", channel.getInetSocketAddress(), message);
//        } else {
//            //log.debug("Receive message from channel {}, {}", channel.getInetSocketAddress(), message);
//        }

        switch (message.getType()) {
            case KEEP_ALIVE_PING:

                log.info("received KEEP_ALIVE_PING from {}",channel.getInetSocketAddress());
                keepAliveService.processMessage(channel, message);
                break;
            case KEEP_ALIVE_PONG:
                log.info("received KEEP_ALIVE_PONG from {}",channel.getInetSocketAddress());
                keepAliveService.processMessage(channel, message);
                break;
            case HANDSHAKE_HELLO:
                log.info("received HANDSHAKE_HELLO from {}",channel.getInetSocketAddress());
                handshakeService.processMessage(channel, message);
                break;
            case STATUS:
                log.info("received STATUS from {}",channel.getInetSocketAddress());
                //nodeDetectService.processMessage(channel, message);
                break;
            case DISCONNECT:
                log.info("received DISCONNECT from {}",channel.getInetSocketAddress());
                channel.close();
                break;
            default:
                throw new P2pException(P2pException.TypeEnum.NO_SUCH_MESSAGE, "type:" + data[0]);
        }
    }
    private static void handMessage(MyChannel channel, byte[] data) throws P2pException {

        if (channel.isDiscoveryMode()) {
            channel.send(new MyP2pDisconnectMessage(Connect.DisconnectReason.DISCOVER_MODE));
            channel.getCtx().close();
            return;
        }

        if (!channel.isFinishHandshake()) {
            channel.setFinishHandshake(true);
            DisconnectCode code = processPeer(channel);
            if (!DisconnectCode.NORMAL.equals(code)) {
                Connect.DisconnectReason disconnectReason = getDisconnectReason(code);
                channel.send(new MyP2pDisconnectMessage(disconnectReason));
                channel.getCtx().close();
                return;
            }
            MyConfig.hp2pEventHandler.onConnect(channel);
        }

        MyConfig.hp2pEventHandler.onMessage(channel, data);
    }
    public static Connect.DisconnectReason getDisconnectReason(DisconnectCode code) {
        Connect.DisconnectReason disconnectReason;
        switch (code) {
            case DIFFERENT_VERSION:
                disconnectReason = Connect.DisconnectReason.DIFFERENT_VERSION;
                break;
            case TIME_BANNED:
                disconnectReason = Connect.DisconnectReason.RECENT_DISCONNECT;
                break;
            case DUPLICATE_PEER:
                disconnectReason = Connect.DisconnectReason.DUPLICATE_PEER;
                break;
            case TOO_MANY_PEERS:
                disconnectReason = Connect.DisconnectReason.TOO_MANY_PEERS;
                break;
            case MAX_CONNECTION_WITH_SAME_IP:
                disconnectReason = Connect.DisconnectReason.TOO_MANY_PEERS_WITH_SAME_IP;
                break;
            default: {
                disconnectReason = Connect.DisconnectReason.UNKNOWN;
            }
        }
        return disconnectReason;
    }
    public static synchronized void updateNodeId(MyChannel channel, String nodeId) {
        channel.setNodeId(nodeId);
//        if (nodeId.equals(Hex.toHexString(MyConfig.getLocalId()))) {
//            //log.warn("Channel {} is myself", channel.getInetSocketAddress());
//            channel.send(new MyP2pDisconnectMessage(Connect.DisconnectReason.DUPLICATE_PEER));
//            channel.close();
//            return;
//        }

        List<MyChannel> list = new ArrayList<>();
        channels.values().forEach(c -> {
            if (nodeId.equals(c.getNodeId())) {
                list.add(c);
            }
        });
        if (list.size() <= 1) {
            return;
        }

    }

    //    public static void triggerConnect(InetSocketAddress address) {
//        connPoolService.triggerConnect(address);
//    }
    public static void logDisconnectReason(MyChannel channel, Connect.DisconnectReason reason) {
        log.info("Try to close channel: {}, reason: {}", channel.getInetSocketAddress(), reason.name());
}


}
