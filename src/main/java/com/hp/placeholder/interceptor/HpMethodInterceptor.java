package com.hp.placeholder.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * aop
 */
public class HpMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        invocation.getMethod().toString();
        invocation.proceed();
        invocation.getArguments();
        invocation.getMethod();
        return null;
    }
}
