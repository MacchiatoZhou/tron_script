package myConnection;

import lombok.Getter;

import java.util.Set;

public abstract class MyP2pEventHandler {
    @Getter
    protected Set<Byte> messageTypes;

    public void onConnect(MyChannel channel) {
    }

    public void onDisconnect(MyChannel channel) {
    }

    public void onMessage(MyChannel channel, byte[] data) {
    }
}
