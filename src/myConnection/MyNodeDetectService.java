package myConnection;

import org.tron.p2p.connection.Channel;
import org.tron.p2p.connection.message.Message;

public class MyNodeDetectService {
    private final String testNum="test";
    public void  processMessage(Channel channel, Message message){
        //do nothing for now
        System.out.printf(testNum);
    }
}
