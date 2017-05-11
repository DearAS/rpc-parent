1.RPC-MAVEN项目结构
    rpcparent
        rpc-entity
        rpc-common
        rpc-providers
        rpc-customer
        rpc-interface
2.服务提供者发布服务，只需要打一个注解即可发布服务。
    1.创建RpcServer注解
    2.在Spring容器加载完毕之后，获取所有RpcService注解的类,注册到zk中。
        2.1 将所有打了RpcService注解的类发布到zk中
        2.2 zk中的zNode:接口名/providers/ip:接口名:应用名:timestamp
    3.在本机开启一个Netty服务端，并监听端口
        dubbo是每个接口一个端口。自定义的rpc是所有的接口使用同一个端口链接。
3.客户端使用注解@Autowired主动注入：
    3.1 自动注入的是proxy对象
    3.2 创建ClientProxy对象，创建create方法，传入class获取一个加强的proxy
    3.3 在proxy中使用netty创建socket访问netty服务端，并返回结果进行return。
    3.4 客户端正常使用接口调用方法即可。