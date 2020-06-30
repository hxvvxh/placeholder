package com.hp.placeholder.wxb.aop;

import com.hp.placeholder.wxb.aop.annotion.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;

/**
 * aop spring-boot-starter-aop
 */
@Aspect
@Slf4j
public class LogAspect {
    /**
     * 定义公共的切点表达式
     */
    @Pointcut("@annotation(com.hp.placeholder.wxb.aop.annotion.Log) && @within(com.hp.placeholder.wxb.aop.annotion.Log)")
    public void pointCut(){};

    /**
     * 切点之前
     * @param joinPoint
     */
    @Before("pointCut()")
    public void logBefore(JoinPoint joinPoint){
        String method=joinPoint.getSignature().getName();
        String clazz=joinPoint.getTarget().getClass().getSimpleName();
        String modifier=Modifier.toString(joinPoint.getSignature().getModifiers());
        Object[] args=joinPoint.getArgs();

//        String logg=logs.log();
//        String value=logs.value();
        log.info("before {}#{}#{},args:{}",clazz,method,modifier,args);
//        log.info("hp.Log,log:{},value:{}",logg,value);
    }


    /**
     * 切点之后
     * @param joinPoint
     */
    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint){
        String method=joinPoint.getSignature().getName();
        String clazz=joinPoint.getTarget().getClass().getSimpleName();
        log.info("end {}#{},args:{}",clazz,method);
    }

    /**
     * 返回数据之前
     * @param result
     */
    @AfterReturning(value = "pointCut()",returning = "result")
    public void logReturn(Object result){
        log.info("result:{}",result);
    }
}
