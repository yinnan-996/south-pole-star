package com.test;

public class Service2 implements IService {

    @Override
    public void say(String name) {
        System.out.println("service2:"+name);
    }
}
