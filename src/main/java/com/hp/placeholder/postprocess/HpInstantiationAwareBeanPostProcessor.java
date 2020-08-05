package com.hp.placeholder.postprocess;

import com.hp.placeholder.interceptor.HpMethodInterceptor2;
import com.hp.placeholder.postprocess.bean.HpInstantition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

/**
 * 十一：springboot bean的生命周期
 *
 *  BeanPostProcessor && InstantiationAwareBeanPostProcessor
 *
 *         (1)初始化： 是一个赋值的过程，使用get，set方法为bean设置属性
 *                         BeanPostProcessor ： 作用在初始化前后
 *                                     ：postProcessBeforeInitialization()/postProcessAfterInitialization()
 *         (2)实例化： 一个创建bean的过程，即调用bean的构造函数创建单例bean，并将bean放入单例池中
 *                 InstantiationAwareBeanPostProcessor ： 作用在实例化bean的前后
 *                             :postProcessBeforeInstantiation()/postProcessAfterInstantiation()/postProcessProperties()
 *
 *         (3)执行顺序：先初始化：执行BeanPostProcessor
 *                                       AbstractAutowireCapableBeanFactory#initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
 *                                      -> postProcessBeforeInitialization() 初始化前置工作（AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization(java.lang.Object, java.lang.String)）
 *                                        ->postProcessAfterInitialization() 初始化后置工作（AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization(java.lang.Object, java.lang.String)）
 *                      后实例化：执行InstantiationAwareBeanPostProcessor
 *                                          AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition)
 *                                      -> postProcessBeforeInstantiation() 实例化前置工作（AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation(java.lang.Class, java.lang.String)）
 *                                      -> postProcessAfterInstantiation()  实例化后置工作（AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization(java.lang.Object, java.lang.String)）
 *                                      -> postProcessProperties() 为实例化的bean 做各种的赋值操作
 *
 *   AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation代码中有一点需要注意：
 *      postProcessBeforeInstantiation() 实例化前如果返回的object为null 则直接返回创建bean
 *          不为null->说明修改bean对象，此时执行postProcessAfterInitialization() 直接执行初始化后置方法，实例化后置和初始化前置都不执行
 *      postProcessAfterInitialization() 初始化后置方法如果返回null，则直接返回创建bean
 *          不为null-> 哪个这个bean直接返回ioc容器，进行初始化之后的操作
 *
 *
 *
 *  网上的总结：借鉴
 *  InstantiationAwareBeanPostProcessor接口继承BeanPostProcessor接口，它内部提供了3个方法，
 *  再加上BeanPostProcessor接口内部的2个方法，所以实现这个接口需要实现5个方法。
 *  InstantiationAwareBeanPostProcessor接口的主要作用在于目标对象的实例化过程中需要处理的事情，包括实例化对象的前后过程以及实例的属性设置
 *
 *
 * postProcessBeforeInstantiation方法是最先执行的方法，它在目标对象实例化之前调用，该方法的返回值类型是Object，
 * 我们可以返回任何类型的值。由于这个时候目标对象还未实例化，所以这个返回值可以用来代替原本该生成的目标对象的实例(比如代理对象)。
 * 如果该方法的返回值代替原本该生成的目标对象，后续只有postProcessAfterInitialization方法会调用，其它方法不再调用；否则按照正常的流程走
 *
 *
 * postProcessAfterInstantiation方法在目标对象实例化之后调用，这个时候对象已经被实例化，但是该实例的属性还未被设置，都是null。
 * 因为它的返回值是决定要不要调用postProcessPropertyValues方法的其中一个因素（因为还有一个因素是mbd.getDependencyCheck()）；
 * 如果该方法返回false,并且不需要check，那么postProcessPropertyValues就会被忽略不执行；如果返回true，postProcessPropertyValues就会被执行
 *
 *
 * postProcessPropertyValues方法对属性值进行修改(这个时候属性值还未被设置，但是我们可以修改原本该设置进去的属性值)。
 * 如果postProcessAfterInstantiation方法返回false，该方法可能不会被调用。可以在该方法内对属性值进行修改
 * 父接口BeanPostProcessor的2个方法postProcessBeforeInitialization和postProcessAfterInitialization都是在目标对象被实例化之后，
 * 并且属性也被设置之后调用的
 *
 *
 * Instantiation表示实例化，Initialization表示初始化。实例化的意思在对象还未生成，初始化的意思在对象已经生成
 */
//@Component
//@Slf4j
//public class HpInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
//    @Override
//    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
//        if (beanClass == HpInstantition.class){
//            Enhancer enhancer=new Enhancer();
//            enhancer.setSuperclass(beanClass);
//            enhancer.setCallback(new HpMethodInterceptor2());
//            HpInstantition hpInstantition= (HpInstantition) enhancer.create();
//            hpInstantition.setUser("postProcessBeforeInstantiation");
//            return hpInstantition;
//        }
//        return null;
//    }
//
//    @Override
//    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
//        return false;
//    }
//
//    @Override
//    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
//        return null;
//    }
//}
