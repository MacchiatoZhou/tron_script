package myConnection.higher.MyHMessage;

import myDiscover.MyConfig;
import com.google.protobuf.ByteString;
import lombok.Getter;
import myDiscover.Tool;
import org.apache.commons.lang3.StringUtils;
import org.tron.common.utils.StringUtil;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.net.message.MessageTypes;
import org.tron.p2p.discover.Node;
import org.tron.p2p.utils.ByteArray;
import org.tron.protos.Discover.Endpoint;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.HelloMessage.Builder;

@Getter
public class MyHHelloMessage extends MyTronMessage{
    private Protocol.HelloMessage helloMessage;

    public MyHHelloMessage(byte type, byte[] rawData) throws Exception {
        super(type, rawData);
        this.helloMessage = Protocol.HelloMessage.parseFrom(rawData);
    }

    public MyHHelloMessage(byte[] data) throws Exception {
        super(MessageTypes.P2P_HELLO.asByte(), data);
        this.helloMessage = Protocol.HelloMessage.parseFrom(data);
    }
    public MyHHelloMessage(){
        Endpoint from = MyConfig.getHomeFrom();
        MyConfig.MyBlockId MyHeadBlockId = MyConfig.getHeadBlockId();
        Protocol.HelloMessage.BlockId hBlockId = Protocol.HelloMessage.BlockId.newBuilder()
                .setHash(MyHeadBlockId.getByteString())
                .setNumber(MyHeadBlockId.getNum())
                .build();
        byte[] genesisBytes = Tool.hexStringToByteArray(MyConfig.getGenesisHashHexString());
        MyConfig.MyBlockId genesisBlockId = new MyConfig.MyBlockId(genesisBytes,0);
        Protocol.HelloMessage.BlockId gBlockId = Protocol.HelloMessage.BlockId.newBuilder()
                .setHash(genesisBlockId.getByteString())
                .setNumber(0)
                .build();
        Builder builder = Protocol.HelloMessage.newBuilder();
        builder.setFrom(from);
        builder.setVersion(MyConfig.getNetwork());
        builder.setTimestamp(System.currentTimeMillis());
        builder.setGenesisBlockId(gBlockId);
        builder.setSolidBlockId(hBlockId);
        builder.setHeadBlockId(hBlockId);
        builder.setNodeType(0);//Full node
        builder.setLowestBlockNum(0);
        builder.setCodeVersion(ByteString.copyFrom(MyConfig.getCodeVersion().getBytes()));

        this.helloMessage = builder.build();
        this.type = MessageTypes.P2P_HELLO.asByte();
        this.data = this.helloMessage.toByteArray();
    }
//    public MyHHelloMessage(Node from, long timestamp) {
//
//        Endpoint fromEndpoint = getEndpointFromNode(from);
//
//        BlockCapsule.BlockId gid = chainBaseManager.getGenesisBlockId();
//        Protocol.HelloMessage.BlockId gBlockId = Protocol.HelloMessage.BlockId.newBuilder()
//                .setHash(gid.getByteString())
//                .setNumber(gid.getNum())
//                .build();
//
//        BlockCapsule.BlockId sid = chainBaseManager.getSolidBlockId();
//        Protocol.HelloMessage.BlockId sBlockId = Protocol.HelloMessage.BlockId.newBuilder()
//                .setHash(sid.getByteString())
//                .setNumber(sid.getNum())
//                .build();
//
//        BlockCapsule.BlockId hid = chainBaseManager.getHeadBlockId();
//        Protocol.HelloMessage.BlockId hBlockId = Protocol.HelloMessage.BlockId.newBuilder()
//                .setHash(hid.getByteString())
//                .setNumber(hid.getNum())
//                .build();
//        Builder builder = Protocol.HelloMessage.newBuilder();
//        builder.setFrom(fromEndpoint);
//        builder.setVersion(MyConfig.getNetwork());
//        builder.setTimestamp(timestamp);
//        builder.setGenesisBlockId(gBlockId);
//        builder.setSolidBlockId(sBlockId);
//        builder.setHeadBlockId(hBlockId);
//        builder.setNodeType(chainBaseManager.getNodeType().getType());
//        builder.setLowestBlockNum(chainBaseManager.isLiteNode()
//                ? chainBaseManager.getLowestBlockNum() : 0);
//        builder.setCodeVersion(ByteString.copyFrom(MyConfig.getCodeVersion().getBytes()));
//
//        this.helloMessage = builder.build();
//        this.type = MessageTypes.P2P_HELLO.asByte();
//        this.data = this.helloMessage.toByteArray();
//    }

    public void setHelloMessage(Protocol.HelloMessage helloMessage) {
        this.helloMessage = helloMessage;
        this.data = this.helloMessage.toByteArray();
    }

    public int getVersion() {
        return this.helloMessage.getVersion();
    }

    public int getNodeType() {
        return this.helloMessage.getNodeType();
    }

    public long getLowestBlockNum() {
        return this.helloMessage.getLowestBlockNum();
    }

    public long getTimestamp() {
        return this.helloMessage.getTimestamp();
    }

    public Node getFrom() {
        Endpoint from = this.helloMessage.getFrom();
        return new Node(from.getNodeId().toByteArray(),
                ByteArray.toStr(from.getAddress().toByteArray()),
                ByteArray.toStr(from.getAddressIpv6().toByteArray()), from.getPort());
    }

    public BlockCapsule.BlockId getGenesisBlockId() {
        return new BlockCapsule.BlockId(this.helloMessage.getGenesisBlockId().getHash(),
                this.helloMessage.getGenesisBlockId().getNumber());
    }

    public BlockCapsule.BlockId getSolidBlockId() {
        return new BlockCapsule.BlockId(this.helloMessage.getSolidBlockId().getHash(),
                this.helloMessage.getSolidBlockId().getNumber());
    }

    public BlockCapsule.BlockId getHeadBlockId() {
        return new BlockCapsule.BlockId(this.helloMessage.getHeadBlockId().getHash(),
                this.helloMessage.getHeadBlockId().getNumber());
    }

    @Override
    public Class<?> getAnswerMessage() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(super.toString())
                .append("from: ").append(getFrom().getInetSocketAddressV4()).append("\n")
                .append("timestamp: ").append(getTimestamp()).append("\n")
                .append("headBlockId: ").append(getHeadBlockId().getString()).append("\n")
                .append("nodeType: ").append(helloMessage.getNodeType()).append("\n")
                .append("lowestBlockNum: ").append(helloMessage.getLowestBlockNum()).append("\n");

        ByteString address = helloMessage.getAddress();
        if (!address.isEmpty()) {
            builder.append("address:")
                    .append(StringUtil.encode58Check(address.toByteArray())).append("\n");
        }

        ByteString signature = helloMessage.getSignature();
        if (!signature.isEmpty()) {
            builder.append("signature:")
                    .append(signature.toByteArray().length).append("\n");
        }

        ByteString codeVersion = helloMessage.getCodeVersion();
        if (!codeVersion.isEmpty()) {
            builder.append("codeVersion:")
                    .append(new String(codeVersion.toByteArray())).append("\n");
        }

        return builder.toString();
    }

    public Protocol.HelloMessage getInstance() {
        return this.helloMessage;
    }

    public boolean valid() {
        byte[] genesisBlockByte = this.helloMessage.getGenesisBlockId().getHash().toByteArray();
        if (genesisBlockByte.length == 0) {
            return false;
        }

        byte[] solidBlockId = this.helloMessage.getSolidBlockId().getHash().toByteArray();
        if (solidBlockId.length == 0) {
            return false;
        }

        byte[] headBlockId = this.helloMessage.getHeadBlockId().getHash().toByteArray();
        if (headBlockId.length == 0) {
            return false;
        }

        int maxByteSize = 200;
        ByteString address = this.helloMessage.getAddress();
        if (!address.isEmpty() && address.toByteArray().length > maxByteSize) {
            return false;
        }

        ByteString sig = this.helloMessage.getSignature();
        if (!sig.isEmpty() && sig.toByteArray().length > maxByteSize) {
            return false;
        }

        ByteString codeVersion = this.helloMessage.getCodeVersion();
        if (!codeVersion.isEmpty() && codeVersion.toByteArray().length > maxByteSize) {
            return false;
        }

        return true;
    }

    public static Endpoint getEndpointFromNode(Node node) {
        Endpoint.Builder builder = Endpoint.newBuilder()
                .setPort(node.getPort());
        if (node.getId() != null) {
            builder.setNodeId(ByteString.copyFrom(node.getId()));
        }
        if (StringUtils.isNotEmpty(node.getHostV4())) {
            builder.setAddress(
                    ByteString.copyFrom(org.tron.p2p.utils.ByteArray.fromString(node.getHostV4())));
        }
        if (StringUtils.isNotEmpty(node.getHostV6())) {
            builder.setAddressIpv6(
                    ByteString.copyFrom(org.tron.p2p.utils.ByteArray.fromString(node.getHostV6())));
        }
        return builder.build();
    }
}
