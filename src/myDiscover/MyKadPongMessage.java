package myDiscover;

import org.tron.p2p.discover.Node;
import org.tron.p2p.discover.message.MessageType;
import org.tron.p2p.discover.message.kad.KadMessage;
import org.tron.p2p.protos.Discover;
import org.tron.p2p.utils.NetUtil;

public class MyKadPongMessage extends KadMessage {
    private final Discover.PongMessage pongMessage;

    public MyKadPongMessage(byte[] data) throws Exception {
        super(MessageType.KAD_PONG, data);
        this.pongMessage = org.tron.p2p.protos.Discover.PongMessage.parseFrom(data);
    }

    public MyKadPongMessage(Node from) {
        super(MessageType.KAD_PONG, (byte[])null);
        Discover.Endpoint toEndpoint = getEndpointFromNode(from);
        this.pongMessage = org.tron.p2p.protos.Discover.PongMessage.newBuilder().setFrom(toEndpoint).setEcho(11111).setTimestamp(System.currentTimeMillis()).build();
        this.data = this.pongMessage.toByteArray();
    }

    /**
     * get default PongMsg
     */
    public MyKadPongMessage(){
        super(MessageType.KAD_PONG, (byte[])null);
        Discover.Endpoint toEndpoint = getEndpointFromNode(MyConfig.getFrom());
        this.pongMessage = org.tron.p2p.protos.Discover.PongMessage.newBuilder().setFrom(toEndpoint).setEcho(11111).setTimestamp(System.currentTimeMillis()).build();
        this.data = this.pongMessage.toByteArray();
    }

    public int getNetworkId() {
        return this.pongMessage.getEcho();
    }

    public long getTimestamp() {
        return this.pongMessage.getTimestamp();
    }

    public Node getFrom() {
        return NetUtil.getNode(this.pongMessage.getFrom());
    }

    public String toString() {
        return "[pongMessage: " + this.pongMessage;
    }

    public boolean valid() {
        return NetUtil.validNode(this.getFrom());
    }
}
