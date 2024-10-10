package myConnection.higher.MyHMessage;

public abstract class MyTronMessage extends org.tron.common.overlay.message.Message{
    public MyTronMessage() {
    }

    public MyTronMessage(byte[] rawData) {
        super(rawData);
    }

    public MyTronMessage(byte type, byte[] rawData) {
        super(type, rawData);
    }
}
