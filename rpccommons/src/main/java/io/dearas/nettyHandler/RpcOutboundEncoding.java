package io.dearas.nettyHandler;

import io.dearas.util.SeriallzationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by tnp on 04/05/2017.
 */
public class RpcOutboundEncoding extends MessageToByteEncoder{


    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        System.out.println("RPC序列化对象"+o.getClass().getName());
        byte[] bytes = SeriallzationUtil.seriallzate(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
