package io.dearas;

import io.dearas.client.RpcProxy;
import io.dearas.service.HelloInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by tnp on 05/05/2017.
 */
@Service("myapp")
public class App {
    @Autowired
    private RpcProxy proxy;

    public void rpcSend(){
        HelloInterface helloService = proxy.create(HelloInterface.class);
        helloService.saySomething("你好！");
    }
    public void rpcGetAdd(){
        HelloInterface helloService = proxy.create(HelloInterface.class);
        helloService.getAdditition(100,100);
    }
    public void rpcGetPerson(){
        HelloInterface helloService = proxy.create(HelloInterface.class);
        helloService.getPerson("vinci");
    }
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:*.xml");

        App app = context.getBean("myapp", App.class);
        //app.rpcSend();
        //app.rpcGetAdd();
        app.rpcGetPerson();
    }
}
