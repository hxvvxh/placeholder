package com.hp.placeholder.factoryBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MyFactoryBean implements FactoryBean<MyFactoryBean.My> {

    /**
     * 容器通过工厂想要获取到的bean
     * @return
     * @throws Exception
     */
    @Override
    public My getObject() throws Exception {
        return new My("韩鹏");
    }

    /**
     * 类型
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return My.class;
    }

    /**
     * 是否单例
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    @Data
    @AllArgsConstructor
    public class My{
        private String my;
    }
}
