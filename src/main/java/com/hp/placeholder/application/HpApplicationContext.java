package com.hp.placeholder.application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class HpApplicationContext implements ApplicationContextAware {
    private ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    public <T> T getBean(Class clazz){
        return (T)context.getBean(clazz);
    }

    public ApplicationContext getContext(){
        return context;
    }
}
