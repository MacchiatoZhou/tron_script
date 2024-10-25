package myDiscover;

import com.google.common.primitives.Longs;
import com.google.protobuf.ByteString;
import lombok.Getter;
import lombok.Setter;
import myConnection.MyHP2pEventHandlerImpl;
import myConnection.MyP2pEventHandler;
import myConnection.higher.MyHMessage.MyHHelloMessage;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.capsule.BlockCapsule;
import org.tron.p2p.discover.Node;
import org.tron.p2p.exception.P2pException;
import org.tron.p2p.protos.Discover;
import org.tron.p2p.utils.ByteArray;
import org.tron.p2p.utils.NetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
public class  MyConfig {
    @Getter
    private static Node from;
    @Getter
    private static Node to;
    @Getter
    private static byte[] id ={-49, -117, 20, 19, -71, 85, -16, 57, 5, -59, -22, -79, -103, -118, -12, 54, 92, -77, 13, 71, 35, 75, 41, -91, -101, -38, -76, -47, -106, 116, -1, 7, -43, 23, -63, 27, 122, 107, -67, 127, -44, 67, 79, 43, 55, 117, -46, 18, 118, 5, 17, 39, -90, 52, 50, 12, 62, 11, 47, 29, -67, -75, 68, -15};
    @Getter@Setter
    private static byte[] localId ={-49, -117, 20, 19, -71, 85, -16, 57, 5, -59, -22, -79, -103, -118, -12, 54, 92, -77, 13, 71, 35, 75, 41, -91, -101, -38, -76, -47, -106, 116, -1, 7, -43, 23, -63, 27, 122, 107, -67, 127, -44, 67, 79, 43, 55, 117, -46, 18, 118, 5, 17, 39, -90, 52, 50, 12, 62, 11, 47, 29, -67, -75, 68, -14};
    @Getter
    private static String localIp= NetUtil.getExternalIpV4();
    @Getter
    private static int fromPort=18899;
    @Getter
    private static int toPort=18888;
    @Getter

    private static String toIp="填入IP";

    @Getter
    private static int network=11111;
    @Getter
    private static String GenesisHashHexString="00000000000000001ebf88508a03865c71d452e25f4d51194196a1d22b6653dc";
    public static volatile List<MyP2pEventHandler> handlerList = new ArrayList<>();
    public static volatile Map<Byte, MyP2pEventHandler> handlerMap = new HashMap<>();

    public static int version = 1;

    public static final int TCP_NETTY_WORK_THREAD_NUM = 0;

    public static final int UDP_NETTY_WORK_THREAD_NUM = 1;

    public static final int NODE_CONNECTION_TIMEOUT = 2000;

    public static final int KEEP_ALIVE_TIMEOUT = 2_000_000_000;

    public static final int PING_TIMEOUT = 20_000;

    public static final int NETWORK_TIME_DIFF = 1000;

    public static final long DEFAULT_BAN_TIME = 60_000;

    public static final int MAX_MESSAGE_LENGTH = 5 * 1024 * 1024;
    @Getter
    public static final String codeVersion = "4.7.6";
    public static final String VERSION_NAME = "GreatVoyage-v4.7.4-44-g8720e06a6";
    public static final String VERSION_CODE = "18306";
    public static MyHP2pEventHandlerImpl hp2pEventHandler;

    @Setter
    @Getter
    public static MyHHelloMessage helloMessageReceive;

    @Setter
    @Getter
    public static MyHHelloMessage helloMessageSend;
//    public static final MyBlockId genesisBlockId=;
//    public static MyBlockId solidBlockId=;
//    public static MyBlockId headBlockId = ;
    private long timestamp;
    public MyConfig(){

    }
    public static void test(){
        System.out.println("localId: "+Tool.encodeToBase64(localId));
    }
    public static void init(){
        to = new Node(id,toIp,"",toPort);
        from = new Node(localId,localIp,"",fromPort);
        System.out.println("localIP: "+localIp);
        hp2pEventHandler = new MyHP2pEventHandlerImpl();
    }
    public static void init(String localId){
        setLocalId(Tool.decodeFromBase64(localId));
        to = new Node(id,toIp,"",toPort);
        from = new Node(Tool.decodeFromBase64(localId),localIp,"",fromPort);
        System.out.println("localIP: "+localIp);
        System.out.println("localId: "+localId);
        hp2pEventHandler = new MyHP2pEventHandlerImpl();
    }
    public static void init(byte[] localId){
        setLocalId(localId);
        to = new Node(id,toIp,"",toPort);
        from = new Node(localId,localIp,"",fromPort);
        System.out.println("localIP: "+localIp);
        System.out.println("localId: "+ Arrays.toString(localId));
        hp2pEventHandler = new MyHP2pEventHandlerImpl();
    }
    public static void setToIp(String toIp){
        to = new Node(id,toIp,"",toPort);
    }
    public static void addP2pEventHandler(MyP2pEventHandler p2pEventHandler) throws P2pException {
        if (p2pEventHandler.getMessageTypes() != null) {
            for (Byte type : p2pEventHandler.getMessageTypes()) {
                if (handlerMap.get(type) != null) {
                    throw new P2pException(P2pException.TypeEnum.TYPE_ALREADY_REGISTERED, "type:" + type);
                }
            }
            for (Byte type : p2pEventHandler.getMessageTypes()) {
                handlerMap.put(type, p2pEventHandler);
            }
        }
        handlerList.add(p2pEventHandler);
    }
    public static Discover.Endpoint getHomeNode() {
        Discover.Endpoint.Builder builder = Discover.Endpoint.newBuilder()
                .setNodeId(ByteString.copyFrom(MyConfig.getLocalId()))
                .setPort(MyConfig.getFromPort());
        if (StringUtils.isNotEmpty(MyConfig.getLocalIp())) {
            builder.setAddress(ByteString.copyFrom(
                    ByteArray.fromString(MyConfig.getLocalIp())));
        }
//        if (StringUtils.isNotEmpty(Parameter.p2pConfig.getIpv6())) {
//            builder.setAddressIpv6(ByteString.copyFrom(
//                    ByteArray.fromString(Parameter.p2pConfig.getIpv6())));
//        }
        return builder.build();
    }
    public static class MyBlockId extends Sha256Hash {

        private long num;

        public MyBlockId() {
            super(Sha256Hash.ZERO_HASH.getBytes());
            num = 0;
        }

        public MyBlockId(Sha256Hash blockId) {
            super(blockId.getBytes());
            byte[] blockNum = new byte[8];
            System.arraycopy(blockId.getBytes(), 0, blockNum, 0, 8);
            num = Longs.fromByteArray(blockNum);
        }

        /**
         * Use {@link #wrap(byte[])} instead.
         */
        public MyBlockId(Sha256Hash hash, long num) {
            super(num, hash);
            this.num = num;
        }

        public MyBlockId(byte[] hash, long num) {
            super(num, hash);
            this.num = num;
        }

        public MyBlockId(ByteString hash, long num) {
            super(num, hash.toByteArray());
            this.num = num;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || (getClass() != o.getClass() && !(o instanceof Sha256Hash))) {
                return false;
            }
            return Arrays.equals(getBytes(), ((Sha256Hash) o).getBytes());
        }

        public String getString() {
            return "Num:" + num + ",ID:" + super.toString();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public int compareTo(Sha256Hash other) {
            if (other.getClass().equals(BlockCapsule.BlockId.class)) {
                long otherNum = ((BlockCapsule.BlockId) other).getNum();
                return Long.compare(num, otherNum);
            }
            return super.compareTo(other);
        }

        public long getNum() {
            return num;
        }
    }
    public static List<Object> getBlockNumberAndHash() {
        System.out.println("entering func getBlockNumberAndHash");
        String apiUrl = "https://apilist.tronscanapi.com/api/block?sort=-balance&start=0&limit=1&producer=&number=&start_timestamp=&end_timestamp=";
        try {
            // Create URL object
            BufferedReader in = getBufferedReader(apiUrl);

            // Read the response into a StringBuilder
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // Close the input stream
            in.close();

            // Parse the response JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray dataArray = jsonResponse.getJSONArray("data");
            if (!dataArray.isEmpty()) {
                JSONObject blockData = dataArray.getJSONObject(0);
                long number = blockData.getInt("number");
                String hash = blockData.getString("hash");
                long timestamp = blockData.getInt("timestamp");

                // Return the extracted values as a formatted string
                System.out.println("get res success");
                return Arrays.asList(number,hash,timestamp);
            } else {
                System.out.println("get res fail, reason: " + response);

                return Arrays.asList(0L,"",0L);
            }

        } catch (Exception e) {
            System.out.println("get res fail, reason: " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList(0L,"",0L);
        }
    }

    private static BufferedReader getBufferedReader(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);

        // Create HttpURLConnection object
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method to GET
        connection.setRequestMethod("GET");
        connection.setRequestProperty("TRON-PRO-API-KEY", "使用你的api");
        // Get the input stream of the connection
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    public static synchronized MyBlockId getHeadBlockId() {
        List<Object> res = getBlockNumberAndHash();
        if (res.size() < 3 || res.get(0) == null || res.get(1) == null || res.get(2) == null) {
            // 处理 res 为 null 或者 res 的大小小于 3 或者 res 的某个元素为 null 的情况
            // 可以抛出异常、记录错误日志或返回一个默认值
            System.out.println("res: " + res);
            throw new IllegalArgumentException("Invalid block data");
        }

        // 确保 res 中的元素都不为 null 后再进行类型转换
        long num;
        long timestamp;
        String hash;
        try {
            num = (long) res.get(0);
            hash = res.get(1).toString();
            timestamp = (long) res.get(2);
        } catch (ClassCastException e) {
            // 处理类型转换异常
            throw new IllegalArgumentException("Invalid type in block data", e);
        }
        byte[] hashBytes =  Tool.hexStringToByteArray(hash);
        //this.timestamp = timestamp;
        System.out.println("hash:"+Arrays.toString(hashBytes));
        System.out.println("num"+num);

        return new MyBlockId(hashBytes,num);
        
    }
     public static org.tron.protos.Discover.Endpoint getHomeFrom(){
        org.tron.protos.Discover.Endpoint.Builder builder = org.tron.protos.Discover.Endpoint.newBuilder()
                .setPort(fromPort);
        builder.setAddress(ByteString.copyFrom(org.tron.p2p.utils.ByteArray.fromString(localIp)));
        builder.setNodeId(ByteString.copyFrom(localId));
        return builder.build();
    }
}

