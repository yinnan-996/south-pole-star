package com.test;

public class Service1 implements IService {
    @Override
    public void say(String name) {
        System.out.println("service1:"+name);
    }
}
