package com.hp.placeholder.placeholder.autoConfig;

import com.hp.placeholder.placeholder.HpPropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author hp
 * @version 1.0
 * @date 2020/5/11 19:55
 */

/**
 * 给容器注入组件
 * 1：包扫描+ 组件注解（@Component、@Service、@Controller、@Repository）
 * 2:@Bean 导入第三方包里的组件
 * 3：@Import 快速给容器导入一个组件
 *
 * @Configuration  源码分析  ConfigurationClassPostProcessor
 * @Bean

 *
 * 阶段1：增强 @Configuration 注解的类
 * 阶段2：Spring 扫描得到所有 @Bean 方法
 * 阶段3：生成 Spring bean 实例

 *
 * 阶段1：
 * ConfigurationClassPostProcessor
 * AnnotationConfigApplicationContext
 * AnnotationConfigServletWebServerApplicationContext
 * ConfigurationClassParser
 *
 *
 *
 *  (1) 根据@Bean 创建注册BeanDefinition
 *
 *   ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry
 *   ConfigurationClassParser#parse方法
 *   parser ——》processConfigurationClass ——》doProcessConfigurationClass
 *   析到对于的信息添加到configClass中
 *   通过解析@Bean注解，获取到所有带有@Bean注解的方法，创建对应的BeanMethod添加到configClass中，回到processConfigBeanDefinitions方法
 *
 *   this.reader.loadBeanDefinitions(configClasses)
 *      --》loadBeanDefinitionsForConfigurationClass
 *          --》loadBeanDefinitionsForBeanMethod （根据@Bean 注解，解析各种属性。元数据以及相关信息 装配 注册beanDefinition）
 *
 *
 *
 *  (2) 根据beanDefinition 得到有@Configuration 的BeanDefinition , Cglib 动态代理方法(有@Bean的方法)
 *
 * ConfigurationClassPostProcessor#postProcessBeanFactory#enhanceConfigurationClasses 方法中遍历了所有的beanDefinition中
 * getAttribute 等于full的(这个是在ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry 中注册进去的，就是为了区分哪些类上标注了@Configuration)
 *  beanDefinition为AbstractBeanDefinition的所有的单例bean
 *
 *  然后循环这些beanDefinition 向其中添加了Attribute(org.springframework.aop.framework.autoproxy.AutoProxyUtils.preserveTargetClass,true) 即使用Cglib动态代理的方式创建
 *  然后在beanDefinition中设置beanClass类型为动态代理的类class XXX $$EnhancerBySpringCGLIB$$697782d0
 *  需要注意的一点是：这里使用Cglib实例化bean，因为jdk动态代理是基于接口，这里就必须使用Cglib去代理
 *  * AbstractApplicationContext -> AbstractApplicationContext: refresh()
 *  * AbstractApplicationContext -> AbstractApplicationContext: invokeBeanFactoryPostProcessors()
 *  * AbstractApplicationContext -> PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors
 *  * PostProcessorRegistrationDelegate -> ConfigurationClassPostProcessor: enhanceConfigurationClasses
 *  * ConfigurationClassPostProcessor -> ConfigurationClassEnhancer: enhance(真正去实现Cglib代理的地方)
 *
 *
 * 阶段2：
 *  使用ConfigurationClassEnhancer 3个 interceptor：BeanMethodInterceptor ，BeanFactoryAwareMethodInterceptor，NoOp.INSTANCE
 *  	private static final Callback[] CALLBACKS = new Callback[] {
 * 			new BeanMethodInterceptor(),
 * 			new BeanFactoryAwareMethodInterceptor(),
 * 			NoOp.INSTANCE
 * 	};
 *  Spring 是通过执行 @Bean 注解的方法生成 bean，而执行方法时，会经过 cglib interceptor 。interceptor 内部则是基于 BeanFactory 去生成bean
 *
 *ConfigurationClassEnhancer#createClass(org.springframework.cglib.proxy.Enhancer)
 *     Enhancer#registerStaticCallbacks(java.lang.Class, org.springframework.cglib.proxy.Callback[])
 *          Enhancer#setCallbacksHelper(java.lang.Class, org.springframework.cglib.proxy.Callback[], java.lang.String)
 *              			Method setter = getCallbacksSetter(type, methodName);
 * 			                setter.invoke(null, new Object[]{callbacks});
 * 	                        为@Configuration注解的类中的方法上(也就是带有@bean的方法)添加callbacks(方法拦截)(就是上面的三个interceptor)，用callbacks去创建方法
 *
 *
 * (1)BeanMethodInterceptor#intercept(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], org.springframework.cglib.proxy.MethodProxy)
 *      BeanAnnotationHelper#determineBeanNameFor(java.lang.reflect.Method) (这里得到的method上带有@Bean的方法)
 *      cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs) 动态代理去代理对象
 *
 * (2)ConfigurationClassEnhancer.BeanFactoryAwareMethodInterceptor#intercept(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], org.springframework.cglib.proxy.MethodProxy)
 *      得到上一步 代理对象中的 XXX$$beanFactory
 *      如果继承了BeanFactoryAware 则执行setBeanFactory（）方法生成
 *
 *阶段3：生成 Spring bean 实例
 * BeanFactory 去生成bean
 *
 */
@Configuration
public class HpPlaceHolderAutoConfigurer {
    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public static HpPropertyPlaceholderConfigurer hpPropertyPlaceholderConfigurer(){
        return new HpPropertyPlaceholderConfigurer();
    }

    /**
     * 使用Condition 对java运行环境进行判断注入bean
     * 单实例bean：默认在容器启动时创建对象
     *
     * @return
     */
    @Bean
    @ConditionalOnExpression(value = "'${spring.profiles.active}'.equals('dev')")
    public Condition buildCondition(){
        //适配linux系统
        Condition condition= (context, metadata) -> {
            ConfigurableListableBeanFactory factory=context.getBeanFactory();
            ClassLoader classLoader=context.getClassLoader();
            Environment environment=context.getEnvironment();
            BeanDefinitionRegistry  registry=context.getRegistry();

            String property=environment.getProperty("os.name");
            if (property.contains("Linux")){
                return true;
            }
            return false;
        };
        //适配windows系统
        Condition conditionWindows =(context, metadata) -> {
            ConfigurableListableBeanFactory factory=context.getBeanFactory();
            ClassLoader classLoader=context.getClassLoader();
            Environment environment=context.getEnvironment();
            BeanDefinitionRegistry  registry=context.getRegistry();

            String property=environment.getProperty("os.name");
            if (property.contains("Windows")){
                return true;
            }
            return false;
        };
        return condition;
    }
}
