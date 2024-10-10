package myDiscover;

import org.tron.p2p.discover.Node;
import org.tron.p2p.discover.message.MessageType;
import org.tron.p2p.discover.message.kad.KadMessage;
import org.tron.p2p.protos.Discover;
import org.tron.p2p.utils.NetUtil;

public class MyKadPingMessage extends KadMessage {
    private final Discover.PingMessage pingMessage;
   public MyKadPingMessage(Node from, Node to){
       super(MessageType.KAD_PING, (byte[])null);
       Discover.Endpoint fromEndpoint = getEndpointFromNode(from);
       Discover.Endpoint toEndpoint = getEndpointFromNode(to);
       this.pingMessage = org.tron.p2p.protos.Discover.PingMessage.newBuilder().setVersion(11111).setFrom(fromEndpoint).setTo(toEndpoint).setTimestamp(System.currentTimeMillis()).build();
       this.data = this.pingMessage.toByteArray();
   }
    /**
     * get default PingMsg
     */
   public MyKadPingMessage(){
       super(MessageType.KAD_PING, (byte[])null);
       Discover.Endpoint fromEndpoint = getEndpointFromNode(MyConfig.getFrom());
       Discover.Endpoint toEndpoint = getEndpointFromNode(MyConfig.getTo());
       this.pingMessage = org.tron.p2p.protos.Discover.PingMessage.newBuilder().setVersion(11111).setFrom(fromEndpoint).setTo(toEndpoint).setTimestamp(System.currentTimeMillis()).build();
       this.data = this.pingMessage.toByteArray();
   }
    public int getNetworkId()
    {
        return this.pingMessage.getVersion();
    }

    public Node getTo() {
        return NetUtil.getNode(this.pingMessage.getTo());
    }

    public long getTimestamp() {
        return this.pingMessage.getTimestamp();
    }

    public Node getFrom() {
        return NetUtil.getNode(this.pingMessage.getFrom());
    }

    public String toString() {
        return "[pingMessage: " + this.pingMessage;
    }

    public boolean valid() {
        return NetUtil.validNode(this.getFrom());
    }
}
