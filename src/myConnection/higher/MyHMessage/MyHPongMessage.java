package myConnection.higher.MyHMessage;

import org.bouncycastle.util.encoders.Hex;
import org.tron.core.net.message.MessageTypes;

public class MyHPongMessage extends MyTronMessage{
    private static final byte[] FIXED_PAYLOAD = Hex.decode("C0");

    public MyHPongMessage() {
        this.type = MessageTypes.P2P_PONG.asByte();
        this.data = FIXED_PAYLOAD;
    }

    public MyHPongMessage(byte type, byte[] rawData) {
        super(type, rawData);
    }

    public MyHPongMessage(byte[] data) {
        super(MessageTypes.P2P_PONG.asByte(), data);
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
        return null;
    }

    @Override
    public MessageTypes getType() {
        return MessageTypes.fromByte(this.type);
    }
}
