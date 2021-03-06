package com.hp.placeholder.wxb.bean;

import com.hp.placeholder.imports.Color;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @Component原理解析
 *AnnotationBeanNameGenerator
 * AnnotationConfigApplicationContext # scan
 * ClassPathBeanDefinitionScanner //入口
 *
 *  AnnotationBeanNameGenerator
 *
 *  refresh();
 *  (1)SpringApplication#prepareContext
 *  项目启动的时候会进入一次generateBeanName(XXX) 方法注册启动主类PlaceholderApplication（环境准备阶段）
 *      org.springframework.boot.BeanDefinitionLoader#load(java.lang.Class)
 *          org.springframework.context.annotation.AnnotatedBeanDefinitionReader#register(java.lang.Class[])
 *              org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean(java.lang.Class, java.lang.String, java.lang.Class[], java.util.function.Supplier, org.springframework.beans.factory.config.BeanDefinitionCustomizer[])
 *                  AnnotationBeanNameGenerator#generateBeanName
 *
 *  (2)AbstractApplicationContext#invokeBeanFactoryPostProcessors
 *   解析主类上的注解属性获取基础扫描包baseScan和带有@Compent，@Service等注解属性
 *      ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry(这里会获取到主类上的注解属性并注册得到beanDefinition) 见HpPlaceHolderAutoConfigurer
 *
 *
 *
 *
 *
 */
@Component
public class Red implements ApplicationContextAware, EmbeddedValueResolverAware, BeanNameAware {
    private ApplicationContext context;

    private StringValueResolver valueResolver;

    private String red;

    private String applicationName;

    private String name;

    /**
     * BeanNameAware 获取spring中的bean名称
     * @param name
     */
    @Override
    public void setBeanName(String name) {
        this.name=name;
    }

    /**
     * ApplicationContextAware 的方法，一般是用于获取spring容器中管理的bean
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    /**
     * EmbeddedValueResolverAware 的方法，一般用于获取spring容器中的StringValueResolver
     * StringValueResolver.resolveStringValue(...) 占位符解析+替换
     *  替换原理：AbstractBeanFactory中会循环所有的StringValueResolver去执行可以替换EL表达式的StringValueResolver去进行替换操作
     * @param resolver
     */
    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
//        applicationName=resolver.resolveStringValue("项目名称：${spring.applicationContext.name}+配置文件后缀:?{spring.profiles.active}");
        this.valueResolver=resolver;

    }

    public String getRed(){
        Color color=context.getBean(Color.class);
        this.red=color.getColor();
        return red;
    }

    public String getName(){
        return name;
    }

    public String getApplicationName() {
        this.applicationName=valueResolver.resolveStringValue("项目名称：${spring.applicationContext.name}+配置文件后缀:?{spring.profiles.active}");
        return applicationName;
    }
}
