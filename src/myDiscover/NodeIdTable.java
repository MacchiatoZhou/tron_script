package myDiscover;

import java.util.Random;

public class NodeIdTable {
    private NodeIdBucket[] nodeIdTable = new NodeIdBucket[17];

    public NodeIdBucket[] getNodeIdTable() {
        return nodeIdTable;
    }

    public void init(NodeId nodeId){
        boolean[] bitNodeId = Tool.byteArrayToBitArray(nodeId.getNodeId());
        Random random = new Random();
        for (int i =0; i<17;i++){
            NodeIdBucket nodeIdBucket = new NodeIdBucket();
            nodeIdBucket.init(nodeId,i);
            this.nodeIdTable[i]=nodeIdBucket;
        }
    }
}
