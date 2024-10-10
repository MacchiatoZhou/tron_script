package myConnection.higher.H_keepalive;

import myConnection.MyChannel;
import myConnection.higher.MyHMessage.MyHPongMessage;
import myConnection.higher.MyHMessage.MyTronMessage;
import org.tron.core.net.message.MessageTypes;

public class MyHKeepAlive {
    public static void processMessage(MyChannel channel, MyTronMessage message){
        if (message.getType().equals(MessageTypes.P2P_PING)) {
            MyHPongMessage msg = new MyHPongMessage();
            channel.send(msg.getSendBytes());
        }
    }
}
