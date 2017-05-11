# rpc-parent
简单版分布式RPC框架，旨在理解RPC的实现方式。

>分为RPC客户端和RPC服务端。RPC客户端打Spring的@Autowired注解注入接口，调用该接口，自动进行远程调用。RPC服务端打自定义RPC注解，则自动发布服务。

* 客户端与服务端通信使用Netty。Nio框架。
* 对象传递用Protostuff序列化。
* 客户端查询服务端ip:port 采用Zookeeper实现。

