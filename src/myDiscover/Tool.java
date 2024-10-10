package myDiscover;

import com.google.protobuf.ByteString;
import myConnection.higher.MyHMessage.*;
import org.apache.commons.lang3.ArrayUtils;
import org.tron.core.exception.P2pException;
import org.tron.core.net.message.MessageTypes;

import java.security.SecureRandom;
import java.util.Base64;

public class Tool {
    public static final String DATA_LEN=", len=";

    public static boolean[] byteArrayToBitArray(Byte[] byteArray) {
        int byteLength = byteArray.length;
        boolean[] bitArray = new boolean[byteLength * 8];

        for (int i = 0; i < byteLength; i++) {
            for (int bit = 0; bit < 8; bit++) {
                // 将每个字节的每一位提取出来，并存储在位数组中
                bitArray[i * 8 + bit] = (byteArray[i] & (1 << (7 - bit))) != 0;
            }
        }

        return bitArray;
    }
    public static void copyFirstIBits(boolean[] source, boolean[] destination, int i) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("源数组和目标数组不能为空。");
        }
        if (i < 0) {
            throw new IllegalArgumentException("要复制的位数 i 不能为负。");
        }
        if (source.length < i) {
            throw new IllegalArgumentException("源数组的长度不足以复制 " + i + " 位。");
        }
        if (destination.length < i) {
            throw new IllegalArgumentException("目标数组的长度不足以复制 " + i + " 位。");
        }

        System.arraycopy(source, 0, destination, 0, i);
    }
    public static Byte[] bitArrayToByteArray(boolean[] bitArray) {
        if (bitArray.length % 8 != 0) {
            throw new IllegalArgumentException("位数组的长度必须是8的倍数。");
        }

        int byteLength = bitArray.length / 8;
        Byte[] byteArray = new Byte[byteLength];

        for (int i = 0; i < byteLength; i++) {
            byte b = 0;
            for (int bit = 0; bit < 8; bit++) {
                if (bitArray[i * 8 + bit]) {
                    b |= (1 << (7 - bit));
                }
            }
            byteArray[i] = b;
        }

        return byteArray;
    }
    public static byte[] decodeFromBase64(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }
    public static String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    public static Byte[] toByteArray(byte[] byteArray) {
        // 创建一个新的 Byte[] 数组，长度与 byteArray 相同
        Byte[] byteObjectArray = new Byte[byteArray.length];

        // 将 byte[] 中的每个元素转换为 Byte 对象，并存储在 Byte[] 中
        for (int i = 0; i < byteArray.length; i++) {
            byteObjectArray[i] = byteArray[i]; // 自动装箱
        }

        return byteObjectArray;
    }
    public static MyTronMessage create(byte[] data) throws Exception {
        boolean isException = false;
        try {
            byte type = data[0];
            byte[] rawData = ArrayUtils.subarray(data, 1, data.length);
            return create(type, rawData);
        } catch (final P2pException e) {
            isException = true;
            throw e;
        } catch (final Exception e) {
            isException = true;
            throw new P2pException(P2pException.TypeEnum.PARSE_MESSAGE_FAILED,
                    "type=" + data[0] + DATA_LEN + data.length + ", error msg: " + e.getMessage());
        } finally {
            if (isException) {
                //MetricsUtil.counterInc(MetricsKey.NET_ERROR_PROTO_COUNT);
            }
        }
    }
    private static MyTronMessage create(byte type, byte[] packed) throws Exception {
        MessageTypes receivedTypes = MessageTypes.fromByte(type);
        if (receivedTypes == null) {
            throw new P2pException(P2pException.TypeEnum.NO_SUCH_MESSAGE,
                    "type=" + type + DATA_LEN + packed.length);
        }
        switch (receivedTypes) {
            case P2P_HELLO:
                return new MyHHelloMessage(packed);
            case P2P_DISCONNECT:
                return new MyHDisconnectMessage(packed);
            case P2P_PING:
                return new MyHPingMessage(packed);
            case P2P_PONG:
                return new MyHPongMessage(packed);
            case TRX:
                return new MyHTransactionMessage(packed);
            case BLOCK:
                return new MyHBlockMessage(packed);
            case TRXS:
                return new MyHTransactionsMessage(packed);
            case INVENTORY:
                return new MyHInventoryMessage(packed);
            case FETCH_INV_DATA:
                return new MyHFetchInvDataMessageMyH(packed);
            case SYNC_BLOCK_CHAIN:
                return new MyHSyncBlockChainMessage(packed);
            case BLOCK_CHAIN_INVENTORY:
                return new MyHChainInventoryMessage(packed);
            case PBFT_COMMIT_MSG:
                return new MyHPbftCommitMessage(packed);
            default:
                throw new P2pException(P2pException.TypeEnum.NO_SUCH_MESSAGE,
                        receivedTypes.toString() + DATA_LEN + packed.length);
        }
    }

    public static ByteString base64ToByteString(String base64String) {
        // Step 1: 去除前导 0
        // 假设前导0是字符 '0'
        base64String = base64String.replaceFirst("^0+", "");

        // Step 2: Base64 解码
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        // Step 3: 创建 ByteString 对象
        return ByteString.copyFrom(decodedBytes);
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static byte[] generateRandomNodeId() {
        // 1. 创建一个 64 字节的随机字节数组
        byte[] randomBytes = new byte[64];
        SecureRandom random = new SecureRandom();
        random.nextBytes(randomBytes);  // 生成随机字节

        return randomBytes;
    }


}

