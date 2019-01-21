package com.test;

import south.pole.star.rpc.support.ProxyFactory;
import south.pole.star.rpc.support.ServiceImplFactory;

public class TestServiceImplFactory {


    ServiceImplFactory serviceImplFactory = new ServiceImplFactory();

    public static void main(String[] args) {
        TestServiceImplFactory testServiceImplFactory = new TestServiceImplFactory();
        testServiceImplFactory.getServiceImplFactory().add(IService.class,"1",new Service1());
        testServiceImplFactory.getServiceImplFactory().add(IService.class,"2",new Service2());

        IService iService = ProxyFactory.getProxyInstance(IService.class,"1");
        iService.say("666");
    }

    public ServiceImplFactory getServiceImplFactory(){
        return serviceImplFactory;
    }

}
