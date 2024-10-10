package myConnection.higher.MyHMessage;

import org.bouncycastle.util.encoders.Hex;
import org.tron.core.net.message.MessageTypes;

public class MyHPingMessage extends MyTronMessage{
    private static final byte[] FIXED_PAYLOAD = Hex.decode("C0");

    public MyHPingMessage() {
        this.type = MessageTypes.P2P_PING.asByte();
        this.data = FIXED_PAYLOAD;
    }

    public MyHPingMessage(byte type, byte[] rawData) {
        super(type, rawData);
    }

    public MyHPingMessage(byte[] data) {
        super(MessageTypes.P2P_PING.asByte(), data);
    }

    @Override
    public byte[] getData() {
        return FIXED_PAYLOAD;
    }


    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Class<?> getAnswerMessage() {
        return MyHPongMessage.class;
    }

    @Override
    public MessageTypes getType() {
        return MessageTypes.fromByte(this.type);
    }
}
