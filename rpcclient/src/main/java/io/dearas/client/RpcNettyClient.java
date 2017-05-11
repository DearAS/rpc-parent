package io.dearas.client;

import io.dearas.entity.RpcRequest;
import io.dearas.entity.RpcResponse;
import io.dearas.nettyHandler.RpcInboundDecoding;
import io.dearas.nettyHandler.RpcOutboundEncoding;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by tnp on 04/05/2017.
 */
public class RpcNettyClient extends ChannelInboundHandlerAdapter{
    private RpcResponse response;

    private final Object obj = new Object();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.response = (RpcResponse) msg;

        // 唤醒线程
        synchronized (obj){
            obj.notifyAll();
        }
    }

    private Bootstrap bootstrap;
    public RpcNettyClient() {
        bootstrap = new Bootstrap();
    }
    public RpcResponse nettySend(String host, Integer port, RpcRequest request) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(); // 不指定模式CPU*2
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host,port))
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new RpcInboundDecoding(RpcResponse.class))
                                .addLast(new RpcOutboundEncoding())
                                .addLast(RpcNettyClient.this);
                    }
                });
        // 等待连接成功
        ChannelFuture future = bootstrap.connect().sync();

        // 发送要请求的数据
        future.channel().writeAndFlush(request).sync();

        // 等待返回数据后关闭连接
        synchronized (obj){
            obj.wait();
        }

        if (response != null){
            // future.channel().closeFuture().sync();
        }
        return response;
    }
}
