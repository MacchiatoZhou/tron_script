package myConnection.message;

import org.tron.p2p.connection.message.MessageType;
import org.tron.p2p.protos.Connect;

public class MyPongMessage extends MyMessage {

  private Connect.KeepAliveMessage keepAliveMessage;

  public MyPongMessage(byte[] data) throws Exception {
    super(MessageType.KEEP_ALIVE_PONG, data);
    this.keepAliveMessage = Connect.KeepAliveMessage.parseFrom(data);
  }

  public MyPongMessage() {
    super(MessageType.KEEP_ALIVE_PONG, null);
    this.keepAliveMessage = Connect.KeepAliveMessage.newBuilder()
      .setTimestamp(System.currentTimeMillis()).build();
    this.data = this.keepAliveMessage.toByteArray();
  }

  public long getTimeStamp() {
    return this.keepAliveMessage.getTimestamp();
  }

  @Override
  public boolean valid() {
    return getTimeStamp() > 0;
      //&& getTimeStamp() <= System.currentTimeMillis() + MyConfig.NETWORK_TIME_DIFF;
  }
}
