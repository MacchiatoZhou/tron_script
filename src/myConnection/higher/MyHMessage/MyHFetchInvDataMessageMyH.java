package myConnection.higher.MyHMessage;

import org.tron.common.utils.Sha256Hash;
import org.tron.core.net.message.MessageTypes;
import org.tron.protos.Protocol.Inventory;
import org.tron.protos.Protocol.Inventory.InventoryType;

import java.util.List;

public class MyHFetchInvDataMessageMyH extends MyHInventoryMessage {


  public MyHFetchInvDataMessageMyH(byte[] packed) throws Exception {
    super(packed);
    this.type = MessageTypes.FETCH_INV_DATA.asByte();
  }

  public MyHFetchInvDataMessageMyH(Inventory inv) {
    super(inv);
    this.type = MessageTypes.FETCH_INV_DATA.asByte();
  }

  public MyHFetchInvDataMessageMyH(List<Sha256Hash> hashList, InventoryType type) {
    super(hashList, type);
    this.type = MessageTypes.FETCH_INV_DATA.asByte();
  }

}
