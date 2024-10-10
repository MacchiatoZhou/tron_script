package myConnection.higher.MyHMessage;

import org.tron.core.capsule.PbftSignCapsule;
import org.tron.core.net.message.MessageTypes;
import org.tron.protos.Protocol.PBFTCommitResult;

public class MyHPbftCommitMessage extends MyTronMessage {

  private PbftSignCapsule pbftSignCapsule;

  public MyHPbftCommitMessage(byte[] data) {
    super(data);
    this.type = MessageTypes.PBFT_COMMIT_MSG.asByte();
    this.pbftSignCapsule = new PbftSignCapsule(data);
  }

  public MyHPbftCommitMessage(PbftSignCapsule pbftSignCapsule) {
    data = pbftSignCapsule.getData();
    this.type = MessageTypes.PBFT_COMMIT_MSG.asByte();
    this.pbftSignCapsule = pbftSignCapsule;
  }

  public PBFTCommitResult getPBFTCommitResult() {
    return getPbftSignCapsule().getPbftCommitResult();
  }

  public PbftSignCapsule getPbftSignCapsule() {
    return pbftSignCapsule;
  }

  @Override
  public Class<?> getAnswerMessage() {
    return null;
  }
  
}
