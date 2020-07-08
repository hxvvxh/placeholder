package com.hp.placeholder.postprocess;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 加载流程：org.springframework.context.support.AbstractApplicationContext#refresh()
 *           org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
 *           org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.util.List)
 *           org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(java.util.Collection, org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
 *           org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
 *
 * beanFactory是bean属性的处理工厂 可以管理bean工厂的所有BeanDefinition
 * BeanDefinition 是bean的注册表。是未初始化的数据，我们可以通过操作BeanDefinition去修改对应bean的属性
 *
 * 注意一点：
 * spring是先对 BeanDefinitionRegistryPostProcessor调用，再去调用 BeanFactoryPostProcessor 这样的方式去保证BeanDefinition是最准确的
 *
 *
 * 这里对HpBeanDefin 的属性进行了修改，相关的注意事项参考HpBeanDefin
 */
@Component
@Slf4j
public class HpBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("postProcessBeanFactory:"+beanFactory);
        String[] beanNames=beanFactory.getBeanDefinitionNames();
        BeanDefinition beanDefin=null;
        for (int i=0;i<beanNames.length;i++){
            BeanDefinition beanDefinition= beanFactory.getBeanDefinition(beanNames[i]);
            log.info("spring starter with BeanDefinition:{}",beanDefinition);
//            Object bean=beanFactory.getBean(beanNames[i]);
//            log.info("spring starter with bean:{}",bean);
            if (beanNames[i].toLowerCase().contains("hpbeandefin")){
                beanDefin=beanDefinition;
            }
            if (Objects.nonNull(beanDefin)){
                MutablePropertyValues propertyValues=beanDefin.getPropertyValues();
                propertyValues.addPropertyValue("pass","pass");
                propertyValues.addPropertyValue("name","hpppname");
                log.info("BeanFactoryPostProcessor change hpBeanDefin BeanDefinition:{}",beanDefinition);
            }
        }
        log.info("HpBeanFactoryPostProcessor:"+beanFactory);
    }
}
