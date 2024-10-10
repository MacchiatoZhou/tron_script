package myConnection.higher.MyHMessage;

import org.tron.core.net.message.MessageTypes;
import org.tron.protos.Protocol;

public class MyHDisconnectMessage extends MyTronMessage{
    private Protocol.DisconnectMessage disconnectMessage;

    public MyHDisconnectMessage(byte type, byte[] rawData) throws Exception {
        super(type, rawData);
        this.disconnectMessage = Protocol.DisconnectMessage.parseFrom(this.data);
    }

    public MyHDisconnectMessage(byte[] data) throws Exception {
        super(MessageTypes.P2P_DISCONNECT.asByte(), data);
        this.disconnectMessage = Protocol.DisconnectMessage.parseFrom(data);
    }

    public MyHDisconnectMessage(Protocol.ReasonCode reasonCode) {
        this.disconnectMessage = Protocol.DisconnectMessage
                .newBuilder()
                .setReason(reasonCode)
                .build();
        this.type = MessageTypes.P2P_DISCONNECT.asByte();
        this.data = this.disconnectMessage.toByteArray();
    }

    public Protocol.ReasonCode getReason() {
        return this.disconnectMessage.getReason();
    }

    public Protocol.ReasonCode getReasonCode() {
        return disconnectMessage.getReason();
    }

    @Override
    public String toString() {
        return new StringBuilder().append(super.toString()).append("reason: ")
                .append(this.disconnectMessage.getReason()).toString();
    }

    @Override
    public Class<?> getAnswerMessage() {
        return null;
    }
}
