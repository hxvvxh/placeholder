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
 *
 * 使用spring提供的注解式@Component 方式创建的BeanDefinition 是ScannedGenericBeanDefinition 里面中存放着 注解，属性，方法，单例/多例，销毁方法，等所有有关bean的定义
 *
 */

/**
 * 详细的看了一下 BeanDefinition是如何创建的 大致的流程是：
 * refash()的invokeBeanFactoryPostProcessors->
 *            invokeBeanDefinitionRegistryPostProcessors->
 *                  ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry
 *
 * 这个是debug得到的流程信息
 * org.springframework.context.support.AbstractApplicationContext#refresh
 * org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors
 * org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.util.List<org.springframework.beans.factory.config.BeanFactoryPostProcessor>)
 * org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors
 * org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry
 * org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions
 *
 * org.springframework.context.annotation.ConfigurationClassParser#parse(java.util.Set<org.springframework.beans.factory.config.BeanDefinitionHolder>)(D:/maven-reponse/org/springframework/spring-context/5.2.2.RELEASE/spring-context-5.2.2.RELEASE.jar!/org/springframework/context/annotation/ConfigurationClassPostProcessor.class:207)
 * 		org.springframework.context.annotation.ConfigurationClassParser#parse(org.springframework.core.type.AnnotationMetadata, java.lang.String)
 * 		org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass
 * 		org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass
 * 		org.springframework.context.annotation.ComponentScanAnnotationParser#parse
 * 		org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan
 * 			org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents
 * 			org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#scanCandidateComponents
 * 			org.springframework.core.type.classreading.CachingMetadataReaderFactory#getMetadataReader (这里根据@Springboot 注解的基础scan去加载资源)
 * 				org.springframework.core.type.classreading.SimpleMetadataReaderFactory#getMetadataReader(org.springframework.core.io.Resource)
 * 				org.springframework.core.type.classreading.SimpleMetadataReader#SimpleMetadataReader
 * 				org.springframework.asm.ClassReader#accept(org.springframework.asm.ClassVisitor, int)
 * 				org.springframework.asm.ClassReader#accept(org.springframework.asm.ClassVisitor, org.springframework.asm.Attribute[], int) ######## 这里是最终读取文件的地方，使用classloader 使用的是最基础的read 得到的是 ScannedGenericBeanDefinition的集合
 *
 * 		org.springframework.context.annotation.AnnotationConfigUtils#applyScopedProxyMode(D:/maven-reponse/org/springframework/spring-context/5.2.2.RELEASE/spring-context-5.2.2.RELEASE.jar!/org/springframework/context/annotation/ClassPathBeanDefinitionScanner.class:131)
 *
 *
 * org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions
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
