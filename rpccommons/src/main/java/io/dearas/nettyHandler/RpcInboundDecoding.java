package io.dearas.nettyHandler;

import io.dearas.util.SeriallzationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by tnp on 04/05/2017.
 */
public class RpcInboundDecoding extends ByteToMessageDecoder{
    private Class<?> clazz;

    public RpcInboundDecoding(Class<?> clazz) {
        this.clazz = clazz;
    }

    // 解码
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("In-Decoder");
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
        //将ByteBuf转换为byte[]
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        //将data转换成object
        Object obj = SeriallzationUtil.deserialize(data, clazz);
        out.add(obj);
    }
}
