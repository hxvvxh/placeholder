package com.hp.placeholder.interceptor;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * core
 */
@Component
public class HpMethodInterceptor2 implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("HpInstantition 方法前"+method+"\n");
        Object object=method.invoke(o,objects);
        System.out.println("object"+object);
        System.out.println("HpInstantition 方法前"+method+"\n");
        return object;
    }
}
