package myDiscover;

import org.tron.p2p.discover.message.Message;

import java.net.InetSocketAddress;
public class UdpEvent {
    private final Message message;
    private final InetSocketAddress address;

    public UdpEvent(Message message, InetSocketAddress address) {
        this.message = message;
        this.address = address;
    }

    public Message getMessage() {
        return message;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
