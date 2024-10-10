package myConnection.message;

import org.tron.p2p.connection.message.MessageType;
import org.tron.p2p.protos.Connect;

public class MyP2pDisconnectMessage extends MyMessage {
    private Connect.P2pDisconnectMessage p2pDisconnectMessage;

    public MyP2pDisconnectMessage(byte[] data) throws Exception {
        super(MessageType.DISCONNECT, data);
        this.p2pDisconnectMessage = Connect.P2pDisconnectMessage.parseFrom(data);
    }

    public MyP2pDisconnectMessage(Connect.DisconnectReason disconnectReason) {
        super(MessageType.DISCONNECT, null);
        this.p2pDisconnectMessage = Connect.P2pDisconnectMessage.newBuilder()
                .setReason(disconnectReason).build();
        this.data = p2pDisconnectMessage.toByteArray();
    }

    private Connect.DisconnectReason getReason() {
        return p2pDisconnectMessage.getReason();
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(super.toString()).append("reason: ")
                .append(getReason()).toString();
    }
}
