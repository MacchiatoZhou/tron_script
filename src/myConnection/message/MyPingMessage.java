package myConnection.message;

import myDiscover.MyConfig;
import org.tron.p2p.connection.message.MessageType;
import org.tron.p2p.protos.Connect;

public class MyPingMessage extends MyMessage {

  private Connect.KeepAliveMessage keepAliveMessage;

  public MyPingMessage(byte[] data) throws Exception {
    super(MessageType.KEEP_ALIVE_PING, data);
    this.keepAliveMessage = Connect.KeepAliveMessage.parseFrom(data);
  }

  public MyPingMessage() {
    super(MessageType.KEEP_ALIVE_PING, null);
    this.keepAliveMessage = Connect.KeepAliveMessage.newBuilder()
      .setTimestamp(System.currentTimeMillis()).build();
    this.data = this.keepAliveMessage.toByteArray();
  }

  public long getTimeStamp() {
    return this.keepAliveMessage.getTimestamp();
  }

  @Override
  public boolean valid() {
    System.out.println("System.currentTimeMillis() + MyConfig.NETWORK_TIME_DIFF = "+System.currentTimeMillis() + MyConfig.NETWORK_TIME_DIFF);
    System.out.println("TimeStamp = "+getTimeStamp());
    return getTimeStamp() > 0;
      //&& getTimeStamp() <= System.currentTimeMillis() + MyConfig.NETWORK_TIME_DIFF;
  }
}
