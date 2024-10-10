package myConnection.handshake;

import myDiscover.MyConfig;
import myConnection.MessageProcess;
import myConnection.MyChannel;
import myConnection.MyChannelManager;
import myConnection.message.MyHelloMessage;
import myConnection.message.MyMessage;
import myConnection.message.MyP2pDisconnectMessage;
import org.tron.p2p.connection.business.handshake.DisconnectCode;
import org.tron.p2p.protos.Connect;

import static org.tron.p2p.connection.ChannelManager.getDisconnectReason;

public class MyHandshakeService implements MessageProcess {
    private final int networkId = MyConfig.getNetwork();

    public void startHandshake(MyChannel channel) {
        sendHelloMsg(channel, DisconnectCode.NORMAL, channel.getStartTime());
    }

    @Override
    public void processMessage(MyChannel channel, MyMessage message) {
        MyHelloMessage msg = (MyHelloMessage) message;

        if (channel.isFinishHandshake()) {
            //log.warn("Close channel {}, handshake is finished", channel.getInetSocketAddress());
            channel.send(new MyP2pDisconnectMessage(Connect.DisconnectReason.DUP_HANDSHAKE));
            channel.close();
            return;
        }

        channel.setHelloMessage(msg);

        DisconnectCode code = MyChannelManager.processPeer(channel);
        if (code != DisconnectCode.NORMAL) {
            if (!channel.isActive()) {
                sendHelloMsg(channel, code, msg.getTimestamp());
            }
            MyChannelManager.logDisconnectReason(channel, getDisconnectReason(code));
            channel.close();
            return;
        }

        MyChannelManager.updateNodeId(channel, msg.getFrom().getHexId());
        if (channel.isDisconnect()) {
            return;
        }

        if (channel.isActive()) {
            if (msg.getCode() != DisconnectCode.NORMAL.getValue()
                    || (msg.getNetworkId() != networkId && msg.getVersion() != networkId)) {
                DisconnectCode disconnectCode = DisconnectCode.forNumber(msg.getCode());
                //v0.1 have version, v0.2 both have version and networkId

                MyChannelManager.logDisconnectReason(channel, getDisconnectReason(disconnectCode));
                channel.close();
                return;
            }
        } else {

            if (msg.getNetworkId() != networkId) {

                sendHelloMsg(channel, DisconnectCode.DIFFERENT_VERSION, msg.getTimestamp());
                MyChannelManager.logDisconnectReason(channel, Connect.DisconnectReason.DIFFERENT_VERSION);
                channel.close();
                return;
            }
            sendHelloMsg(channel, DisconnectCode.NORMAL, msg.getTimestamp());
        }
        channel.setFinishHandshake(true);
        channel.updateAvgLatency(System.currentTimeMillis() - channel.getStartTime());
        MyConfig.hp2pEventHandler.onConnect(channel);
    }

    private void sendHelloMsg(MyChannel channel, DisconnectCode code, long time) {
        MyHelloMessage helloMessage = new MyHelloMessage(code, time);
        channel.send(helloMessage);
    }
}
