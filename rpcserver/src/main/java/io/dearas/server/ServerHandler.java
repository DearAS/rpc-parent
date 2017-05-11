package io.dearas.server;

import io.dearas.entity.RpcRequest;
import io.dearas.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Rpc接受到请求后的处理
 * Created by tnp on 03/05/2017.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{

    private Map<String,Object> handleMap = new HashMap<String,Object>();

    public ServerHandler(Map<String, Object> handleMap) {
        this.handleMap = handleMap;
    }

    /**
     * 处理客户端请求
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response = new RpcResponse();
        try{
            RpcRequest request =(RpcRequest)msg;
            Object ret = getResult(request);
            response.setResult(ret);
            System.out.println(request);
        }catch (Exception err){
            response.setError(err);
        }
        ctx.writeAndFlush(response);
    }

    private Object getResult(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 1.通过接口名查找对象
        Object obj = handleMap.get(request.getInterfaceName());
        // 2.通过反射调用方法
        Class<?> clazz = Class.forName(request.getInterfaceName());
        Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
        return method.invoke(obj, request.getParameters());
    }
}
