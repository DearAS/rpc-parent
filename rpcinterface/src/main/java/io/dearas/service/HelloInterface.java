package io.dearas.service;

import io.dearas.entity.Person;

/**
 * Hello接口-测试接口
 * Created by tnp on 03/05/2017.
 */
public interface HelloInterface {

    void saySomething(String something);

    Integer getAdditition(Integer a,Integer b);

    Person getPerson(String name);
}
