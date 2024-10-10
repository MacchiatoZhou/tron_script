package myDiscover;

import java.util.Random;

public class NodeIdBucket {
    private NodeId[] nodeIdBucket = new NodeId[16];

    public void init(NodeId nodeId, int distance) {
        boolean[] bitNodeId = Tool.byteArrayToBitArray(nodeId.getNodeId());
        Random random = new Random();
        for (int j = 0; j < 16; j++){
            boolean[] tmpBitNodeId = new boolean[512];
            Tool.copyFirstIBits(bitNodeId,tmpBitNodeId,distance);
            tmpBitNodeId[distance+1]=!bitNodeId[distance+1];
            for (int k=distance+2;k<512;k++){
                tmpBitNodeId[k]=random.nextBoolean();
            }
            Byte[] tmpNodeId = Tool.bitArrayToByteArray(tmpBitNodeId);
            NodeId newNodeId = new NodeId(tmpNodeId);
            //newNodeId.setNodeId(tmpNodeId);
            this.nodeIdBucket[j]=newNodeId;
        }
    }

    public NodeId[] getNodeBucket() {
        return nodeIdBucket;
    }
}
