package myConnection;

import myConnection.message.MyMessage;

public interface MessageProcess {
    void processMessage(MyChannel channel, MyMessage message);
}
