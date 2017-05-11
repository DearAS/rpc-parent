package io.dearas.rpcservice;

import io.dearas.RpcProviders;
import io.dearas.entity.Person;
import io.dearas.service.HelloInterface;

/**
 * Created by tnp on 04/05/2017.
 */
@RpcProviders(interfaceName =  HelloInterface.class)
public class HelloServiceImpl implements HelloInterface {
    public void saySomething(String something) {
        System.out.println("===========RPC-Impl实现===========");
        System.out.println("=================================");
        System.out.println(something);
        System.out.println("=================================");
        System.out.println("===========RPC-Impl结束===========");
    }

    public Integer getAdditition(Integer a, Integer b) {
        System.out.println("===========RPC-Impl实现===========");
        System.out.println("=================================");
        System.out.println(a + "===" + b);
        System.out.println("=================================");
        System.out.println("===========RPC-Impl结束===========");
        return a+b;
    }

    public Person getPerson(String name) {
        System.out.println("===========RPC-Impl实现===========");
        System.out.println("=================================");
        if("vinci".equalsIgnoreCase(name)) {
            Person person = new Person();
            person.setName("Vinci-刘");
            person.setAge(25);
            person.setSex("女(Girl)");
            person.setFavorite("日语");
            System.out.println("=================================");
            System.out.println("===========RPC-Impl结束===========");
            return person;
        }else{
            return null;
        }

    }
}
