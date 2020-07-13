package com.hp.placeholder.postprocess;

import com.hp.placeholder.postprocess.bean.HpBeanDefin2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * BeanDefinitionRegistryPostProcessor
 * BeanDefinitionRegistryPostProcessor 继承了BeanFactoryPostProcessor
 * 在refash的时候：先执行BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry
 *                 再执行BeanFactoryPostProcessor #postProcessBeanFactory
 * BeanDefinition的注册工厂，提供了BeanDefinition自定义并存放容器中的方法
 * BeanDefinitionBuilder 为beanDefinition的建造工厂，使用build设计模式。
 *
 */
@Slf4j
@Component
public class HpBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        log.info("postProcessBeanDefinitionRegistry#registry:{}",registry);
        BeanDefinition beanDefinition=BeanDefinitionBuilder.genericBeanDefinition(HpBeanDefin2.class)
                .addPropertyValue("ref","refHp")
//                .addPropertyReference("hpBea","hpBeanDefin3")
                .setInitMethodName("init")
                .addAutowiredProperty("hpBeanDefin")
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        registry.registerBeanDefinition("hpBeanDefin2",beanDefinition);
        log.info("registry hpBeanDefin2 success");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] names=beanFactory.getBeanDefinitionNames();
        ((List<String>)CollectionUtils.arrayToList(names)).forEach(c -> {
            if (c.toLowerCase().contains("hpbeandefin2")){
                log.info("hpbeandefin2:"+beanFactory.getBeanDefinition(c));
            }
        });
    }
}
