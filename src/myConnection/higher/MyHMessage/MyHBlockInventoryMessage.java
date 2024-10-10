package myConnection.higher.MyHMessage;

import org.tron.core.capsule.BlockCapsule.BlockId;
import org.tron.core.net.message.MessageTypes;
import org.tron.protos.Protocol.BlockInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyHBlockInventoryMessage extends MyTronMessage {

  protected BlockInventory blockInventory;

  public MyHBlockInventoryMessage(byte[] data) throws Exception {
    super(data);
    this.type = MessageTypes.BLOCK_INVENTORY.asByte();
    this.blockInventory = BlockInventory.parseFrom(data);
  }

  public MyHBlockInventoryMessage(List<BlockId> blockIds, BlockInventory.Type type) {
    BlockInventory.Builder invBuilder = BlockInventory.newBuilder();
    blockIds.forEach(blockId -> {
      BlockInventory.BlockId.Builder b = BlockInventory.BlockId.newBuilder();
      b.setHash(blockId.getByteString());
      b.setNumber(blockId.getNum());
      invBuilder.addIds(b);
    });

    invBuilder.setType(type);
    blockInventory = invBuilder.build();
    this.type = MessageTypes.BLOCK_INVENTORY.asByte();
    this.data = blockInventory.toByteArray();
  }

  @Override
  public Class<?> getAnswerMessage() {
    return null;
  }

  private BlockInventory getBlockInventory() {
    return blockInventory;
  }

  public List<BlockId> getBlockIds() {
    return getBlockInventory().getIdsList().stream()
        .map(blockId -> new BlockId(blockId.getHash(), blockId.getNumber()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

}
