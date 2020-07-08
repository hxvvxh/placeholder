package com.hp.placeholder.postprocess.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 这里使用了 BeanFactoryPostProcessor中去获取的BeanDefinition修改pass属性
 * 这里需要注意的一个点是:这里必须需要一个无参的构造方法
 * @@see com.hp.placeholder.postprocess.HpBeanFactoryPostProcessor
 * 至于为什么这个还不是很清楚，我猜的可能是：spring在初始化bean的时候如果没有无参的构造函数时
 * 发现了有参的构造函数就去使用这个去初始化数据 而我们对BeanDefinition 做了修改(在PropertyValue中添加了替换的属性)，可能会导致实例化不成功
 */
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class HpBeanDefin {

    private String name;

    private String pass;

    @PostConstruct
    public void init(){
        this.name="hpBeanDefin";
    }
}
