package io.dearas.client;

import io.dearas.entity.RpcRequest;
import io.dearas.entity.RpcResponse;
import io.dearas.zookeeper.ZkUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by tnp on 04/05/2017.
 */
public class RpcProxy {

    // 创建接口的代理对象
    public <T> T create(Class<T> clazz){
        // 利用JDK的动态代理 创建该Class的代理对象
        Object instance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 封装成一个request对象
                        RpcRequest request = new RpcRequest();
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        // 查找zk中的接口
                        String addr = ZkUtils.getAddrFromInterface(request.getInterfaceName());
                        // 发送Netty请求
                        if(!StringUtils.isEmpty(addr)){
                            String[] split = addr.split(":");
                            RpcNettyClient client = new RpcNettyClient();
                            RpcResponse response = client.nettySend(split[0], Integer.valueOf(split[1]), request);
                            if(response.getError() != null){
                                throw response.getError();
                            }
                            return response.getResult();
                        }else{
                            System.out.println("没有服务提供者！");
                            return null;
                        }
                    }
                });
        return (T) instance;
    }
}