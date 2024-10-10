package myConnection.message;

import org.apache.commons.lang3.ArrayUtils;
import org.tron.p2p.connection.message.MessageType;
import org.tron.p2p.exception.P2pException;

public abstract class MyMessage {
    protected MessageType type;
    protected byte[] data;

    public MyMessage(MessageType type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return this.type;
    }

    public byte[] getData() {
        return this.data;
    }

    public byte[] getSendData() {
        return ArrayUtils.add(this.data, 0, type.getType());
    }

    public abstract boolean valid();

    public boolean needToLog() {
        return type.equals(MessageType.DISCONNECT) || type.equals(MessageType.HANDSHAKE_HELLO);
    }

    public static MyMessage parse(byte[] encode) throws P2pException {
        byte type = encode[0];
        try {
            byte[] data = ArrayUtils.subarray(encode, 1, encode.length);
            MyMessage message;
            switch (MessageType.fromByte(type)) {
                case KEEP_ALIVE_PING:
                    message = new MyPingMessage(data);
                    break;
                case KEEP_ALIVE_PONG:
                    message = new MyPongMessage(data);
                    break;
                case HANDSHAKE_HELLO:
                    message = new MyHelloMessage(data);
                    break;
                case STATUS:
                    message = new MyStatusMessage(data);
                    break;
                case DISCONNECT:
                    message = new MyP2pDisconnectMessage(data);
                    break;
                default:
                    throw new P2pException(P2pException.TypeEnum.NO_SUCH_MESSAGE, "type=" + type);
            }
            if (!message.valid()) {
                throw new P2pException(P2pException.TypeEnum.BAD_MESSAGE, "type=" + type);
            }
            return message;
        } catch (P2pException p2pException) {
            throw p2pException;
        } catch (Exception e) {
            throw new P2pException(P2pException.TypeEnum.BAD_MESSAGE, "type:" + type);
        }
    }

    @Override
    public String toString() {
        return "type: " + getType() + ", ";
    }
}
