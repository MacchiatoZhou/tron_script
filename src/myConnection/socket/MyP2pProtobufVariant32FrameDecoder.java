package myConnection.socket;

import myDiscover.MyConfig;
import myConnection.MyChannel;
import myConnection.message.MyP2pDisconnectMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.extern.slf4j.Slf4j;
import org.tron.p2p.protos.Connect;

import java.util.List;
@Slf4j(topic = "net")
public class MyP2pProtobufVariant32FrameDecoder extends ByteToMessageDecoder {
    private final MyChannel channel;

    public MyP2pProtobufVariant32FrameDecoder(MyChannel channel) {
        this.channel = channel;
    }

    private static int readRawVariant32(ByteBuf buffer) {
        if (!buffer.isReadable()) {
            return 0;
        }
        buffer.markReaderIndex();
        byte tmp = buffer.readByte();
        if (tmp >= 0) {
            return tmp;
        } else {
            int result = tmp & 127;
            if (!buffer.isReadable()) {
                buffer.resetReaderIndex();
                return 0;
            }
            if ((tmp = buffer.readByte()) >= 0) {
                result |= tmp << 7;
            } else {
                result |= (tmp & 127) << 7;
                if (!buffer.isReadable()) {
                    buffer.resetReaderIndex();
                    return 0;
                }
                if ((tmp = buffer.readByte()) >= 0) {
                    result |= tmp << 14;
                } else {
                    result |= (tmp & 127) << 14;
                    if (!buffer.isReadable()) {
                        buffer.resetReaderIndex();
                        return 0;
                    }
                    if ((tmp = buffer.readByte()) >= 0) {
                        result |= tmp << 21;
                    } else {
                        result |= (tmp & 127) << 21;
                        if (!buffer.isReadable()) {
                            buffer.resetReaderIndex();
                            return 0;
                        }
                        result |= (tmp = buffer.readByte()) << 28;
                        if (tmp < 0) {
                            throw new CorruptedFrameException("malformed variant.");
                        }
                    }
                }
            }
            return result;
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        in.markReaderIndex();
        int preIndex = in.readerIndex();
        int length = readRawVariant32(in);
        if (length >= MyConfig.MAX_MESSAGE_LENGTH) {
            log.warn("Receive a big msg or not encoded msg, host : {}, msg length is : {}",
                    ctx.channel().remoteAddress(), length);
            in.clear();
            channel.send(new MyP2pDisconnectMessage(Connect.DisconnectReason.BAD_MESSAGE));
            channel.close();
            return;
        }
        if (preIndex == in.readerIndex()) {
            return;
        }
        if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
        } else {
            out.add(in.readRetainedSlice(length));
        }
    }
}
