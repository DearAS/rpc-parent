package io.dearas.server;

import io.dearas.RpcProviders;

import io.dearas.entity.RpcRequest;
import io.dearas.nettyHandler.RpcInboundDecoding;
import io.dearas.nettyHandler.RpcOutboundEncoding;
import io.dearas.util.IpUtils;
import io.dearas.vo.ZkProvider;
import io.dearas.zookeeper.ZkUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC Netty服务端:在Spring容器加载完毕之后开启Netty服务端程序
 * Created by tnp on 03/05/2017.
 */
public class RpcNettyServer implements ApplicationListener<ContextRefreshedEvent>,ApplicationContextAware {

    public static void main(String[] args) {
        System.out.println(String.class.getCanonicalName());
    }
    // 保存要发布的接口的实现类
    Map<String,Object> interfaceMap = new HashMap<String,Object>();

    /**
     *  Spring容器加载完毕:将打了RpcProvider注解的类发布到zk中
     * @param event
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 注册接口到zk中
        for (Map.Entry<String, Object> entry :interfaceMap.entrySet()) {
            String interfaceName = entry.getKey();
            Object object = entry.getValue();
            ZkProvider provider = new ZkProvider();
            provider.setInterfaceName(interfaceName);
            try {
                provider.setIpAddr(IpUtils.getLocalIp());
            } catch (SocketException e) {
                e.printStackTrace();
            }
            provider.setPort("8000");
            provider.setTimestamp(System.currentTimeMillis());
            // 注册到zk中
            ZkUtils.createProvider(provider);
        }
        // 打开一个Netty连接8000端口
        openNettyServer();
    }

    /**
     * Tip:
     *  Spring会加载base-package下的所有的类。就算没有打入@Service @Controller @Component等注解也会加载到。
     *  通过applicationContext获得。
     *      所以可以实现自定义注解的加载。
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 1.获取所有打了RpcProvider注解的类
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(RpcProviders.class);
        // 2.获取接口名:Bean添加到interfaceMap中
        for (Object obj: map.values()) {
            // 获取全限定类名
            String canonicalName = obj.getClass().getAnnotation(RpcProviders.class).interfaceName().getCanonicalName();
            // 保存全限定类名和对象 到map中
            interfaceMap.put(canonicalName,obj);
        }
    }

    /**
     * 使用Nio IO模式来实现服务端代码的编写。
     * Nio的selector其实是IO多路复用 : 也就是多个客户端的请求可以在同一个线程中进行处理。
     * Nio的面向缓冲区则使IO模型为同步非阻塞模型。这就使得应用中需要不断的轮询socket。
     *  同步非阻塞 + IO多路复用 使得：selector.select(); 可以在有socket连接成功的时候返回。然后再进行非阻塞IO请求处理。
     *  那么伪异步的BIO和Nio之间的区别在哪里呢？
     *      1.都是一个请求一个线程(Netty)
     *      2.当线程池满了，因为是阻塞IO,一个客户端请求时间过长，其他客户端无法连接进来。导致系统假死现象。
     *      3.Nio用一个线程来处理所有用户的请求。不过在请求处理的时候，我们可以用多线程的模式并行处理请求。这样请求的用户再多，也不会造成假死现象。最多会导致请求处理速度变慢。
     *
     */
    private void openNettyServer(){
        // 创建boss线程
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // 装配Netty服务端
        EventLoopGroup eventLoopGroup ;

        try{
            eventLoopGroup = new NioEventLoopGroup();   // 事件线程 同一时间只能处理这么多个请求，其他的排队。
            serverBootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(IpUtils.getLocalIp(),8000)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            // 初始化Netty的处理器 解码 --> 处理 --> 编码 -->发送...
                            channel.pipeline().addLast(new RpcInboundDecoding(RpcRequest.class))
                                    .addLast(new RpcOutboundEncoding())
                                    .addLast(new ServerHandler(interfaceMap));
                        }
                    });
        }catch (Exception err){
            err.printStackTrace();
        }
        try {
            ChannelFuture channelFuture = serverBootstrap.bind().sync();

            channelFuture.channel().closeFuture().sync();   // 开始接受请求
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
