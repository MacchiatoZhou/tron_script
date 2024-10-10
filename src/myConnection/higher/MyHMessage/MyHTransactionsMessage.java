package myConnection.higher.MyHMessage;

import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.net.message.MessageTypes;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Transaction;

import java.util.List;

public class MyHTransactionsMessage extends MyTronMessage {

  private Protocol.Transactions transactions;

  public MyHTransactionsMessage(List<Transaction> trxs) {
    Protocol.Transactions.Builder builder = Protocol.Transactions.newBuilder();
    trxs.forEach(trx -> builder.addTransactions(trx));
    this.transactions = builder.build();
    this.type = MessageTypes.TRXS.asByte();
    this.data = this.transactions.toByteArray();
  }

  public MyHTransactionsMessage(byte[] data) throws Exception {
    super(data);
    this.type = MessageTypes.TRXS.asByte();
    this.transactions = Protocol.Transactions.parseFrom(getCodedInputStream(data));
    if (isFilter()) {
      compareBytes(data, transactions.toByteArray());
      TransactionCapsule.validContractProto(transactions.getTransactionsList());
    }
  }

  public Protocol.Transactions getTransactions() {
    return transactions;
  }

  @Override
  public String toString() {
    return new StringBuilder().append(super.toString()).append("trx size: ")
        .append(this.transactions.getTransactionsList().size()).toString();
  }

  @Override
  public Class<?> getAnswerMessage() {
    return null;
  }

}
