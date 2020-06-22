package com.hp.placeholder.wxb.bean;

import com.hp.placeholder.imports.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 *BeanPostProcessor 提供了两个方法
 * postProcessBeforeInitialization ：在Spring IOC 容器实例化bean之后 初始化方法之前执行
 * postProcessAfterInitialization：在初始化之后执行
 *
 * BeanPostProcessor 的两个方法会在对所有的bean都执行两个方法的操作！！
 *
 * 这里的Color使用@PostConstruct 在初始化之后为属性赋值  这里可以清楚的看到两个打印出的结果的不同
 *
 *
 * Spring IOC 初始化bean-> postProcessBeforeInitialization -> 初始化->@PostConstruct -> postProcessAfterInitialization
 */
@Component
@Slf4j
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.toLowerCase().contains("color")){
            String red=((Color)bean).getColor();
            log.warn("postProcessBeforeInitialization，Color:{}",red);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.toLowerCase().contains("color")){
            String red=((Color)bean).getColor();
            log.warn("postProcessAfterInitialization，Color:{}",red);
        }
        return bean;
    }
}
