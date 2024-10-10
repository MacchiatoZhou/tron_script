package myConnection.higher.H_handshake;

import lombok.extern.slf4j.Slf4j;
import myConnection.MyChannel;
import myConnection.MyChannelManager;
import myConnection.higher.MyHMessage.MyHDisconnectMessage;
import myConnection.higher.MyHMessage.MyHHelloMessage;
import myDiscover.MyConfig;
import org.tron.p2p.utils.ByteArray;
import org.tron.protos.Protocol;
@Slf4j
public class MyHHandshake {

    public static void startHandshake(MyChannel channel) {
        sendHelloMessage(channel);
    }
    public static void processHelloMessage(MyChannel channel, MyHHelloMessage msg) {
//        if (MyConfig.getHelloMessageReceive()!= null) {
//            log.warn("Peer {} receive dup hello message", channel.getInetSocketAddress());
//            channel.send((new MyHDisconnectMessage(Protocol.ReasonCode.DUPLICATE_PEER).getSendBytes()));
//            channel.close();
//            return;
//        }

        MyChannelManager.updateNodeId(channel, msg.getFrom().getHexId());
//        if (peer.isDisconnect()) {
//            logger.info("Duplicate Peer {}", peer.getInetSocketAddress());
//            peer.disconnect(ReasonCode.DUPLICATE_PEER);
//            return;
//        }

        if (!msg.valid()) {
            log.warn("Peer {} invalid hello message parameters, GenesisBlockId: {}, SolidBlockId: {}, "
                            + "HeadBlockId: {}, address: {}, sig: {}, codeVersion: {}",
                    channel.getInetSocketAddress(),
                    ByteArray.toHexString(msg.getInstance().getGenesisBlockId().getHash().toByteArray()),
                    ByteArray.toHexString(msg.getInstance().getSolidBlockId().getHash().toByteArray()),
                    ByteArray.toHexString(msg.getInstance().getHeadBlockId().getHash().toByteArray()),
                    msg.getInstance().getAddress().toByteArray().length,
                    msg.getInstance().getSignature().toByteArray().length,
                    msg.getInstance().getCodeVersion().toByteArray().length);
            channel.send((new MyHDisconnectMessage(Protocol.ReasonCode.UNEXPECTED_IDENTITY).getSendBytes()));
            channel.close();
            return;
        }

        log.info("received P2P_HELLO with lowestBlockNum {}, GenesisBlockId {}, SolidBlockId {}",
                msg.getLowestBlockNum(),
                msg.getGenesisBlockId().getString(),
                msg.getSolidBlockId().getString()
                );
        //peer.setAddress(msg.getHelloMessage().getAddress());

//        if (!relayService.checkHelloMessage(msg, peer.getChannel())) {
//            peer.disconnect(ReasonCode.UNEXPECTED_IDENTITY);
//            return;
//        }
//
//        long headBlockNum = chainBaseManager.getHeadBlockNum();
//        long lowestBlockNum = msg.getLowestBlockNum();
//        if (lowestBlockNum > headBlockNum) {
//            logger.info("Peer {} miss block, lowestBlockNum:{}, headBlockNum:{}",
//                    peer.getInetSocketAddress(), lowestBlockNum, headBlockNum);
//            peer.disconnect(ReasonCode.LIGHT_NODE_SYNC_FAIL);
//            return;
//        }
//
//        if (msg.getVersion() != Args.getInstance().getNodeP2pVersion()) {
//            logger.info("Peer {} different p2p version, peer->{}, me->{}",
//                    peer.getInetSocketAddress(), msg.getVersion(),
//                    Args.getInstance().getNodeP2pVersion());
//            peer.disconnect(ReasonCode.INCOMPATIBLE_VERSION);
//            return;
//        }
//
//        if (!Arrays.equals(chainBaseManager.getGenesisBlockId().getBytes(),
//                msg.getGenesisBlockId().getBytes())) {
//            logger.info("Peer {} different genesis block, peer->{}, me->{}",
//                    peer.getInetSocketAddress(),
//                    msg.getGenesisBlockId().getString(),
//                    chainBaseManager.getGenesisBlockId().getString());
//            peer.disconnect(ReasonCode.INCOMPATIBLE_CHAIN);
//            return;
//        }
//
//        if (chainBaseManager.getSolidBlockId().getNum() >= msg.getSolidBlockId().getNum()
//                && !chainBaseManager.containBlockInMainChain(msg.getSolidBlockId())) {
//            logger.info("Peer {} different solid block, peer->{}, me->{}",
//                    peer.getInetSocketAddress(),
//                    msg.getSolidBlockId().getString(),
//                    chainBaseManager.getSolidBlockId().getString());
//            peer.disconnect(ReasonCode.FORKED);
//            return;
//        }
//
//        if (msg.getHeadBlockId().getNum() < chainBaseManager.getHeadBlockId().getNum()
//                && peer.getInetSocketAddress().equals(effectiveCheckService.getCur())) {
//            logger.info("Peer's head block {} is below than we, peer->{}, me->{}",
//                    peer.getInetSocketAddress(), msg.getHeadBlockId().getNum(),
//                    chainBaseManager.getHeadBlockId().getNum());
//            peer.disconnect(ReasonCode.BELOW_THAN_ME);
//            return;
//        }

        MyConfig.setHelloMessageReceive(msg);

        channel.updateAvgLatency(
                System.currentTimeMillis() - channel.getStartTime());
        //PeerManager.sortPeers();
        //peer.onConnect();
    }

    private static void sendHelloMessage(MyChannel channel) {
        MyHHelloMessage message = new MyHHelloMessage();
        //relayService.fillHelloMessage(message, peer.getChannel());
        log.info("send P2P_HELLO to channel {}",channel.getInetSocketAddress());
        log.info("My P2P Hello {}",message);
        channel.send(message.getSendBytes());
        channel.setHHelloMessage(message);
        MyConfig.setHelloMessageSend(message);
    }
}
