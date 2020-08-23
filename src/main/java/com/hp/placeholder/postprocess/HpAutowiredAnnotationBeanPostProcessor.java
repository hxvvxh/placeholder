package com.hp.placeholder.postprocess;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * @Autowired
 * @Value 注解原理分析
 *  AutowiredAnnotationBeanPostProcessor
 *
 *  AutowiredAnnotationBeanPostProcessor 继承了 InstantiationAwareBeanPostProcessor/BeanPostProcessor  初始化、实例化(这两个接口请参考HpInstantiationAwareBeanPostProcessor 这个类)
 *
 *  看源码的入口：MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition
 *                InstantiationAwareBeanPostProcessor # postProcessProperties
 *
 *  (1)postProcessMergedBeanDefinition 执行时机：AbstractApplicationContext#finishBeanFactoryInitialization(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
 *                                              AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors(org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Class, java.lang.String)
 *
 *
 *
 */
@Component
@Slf4j
public class HpAutowiredAnnotationBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        return super.postProcessProperties(pvs, bean, beanName);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessAfterInitialization(bean, beanName);
    }
}
