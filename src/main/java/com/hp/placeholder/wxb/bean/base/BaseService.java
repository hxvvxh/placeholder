package com.hp.placeholder.wxb.bean.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
@Slf4j
public class BaseService implements InitializingBean, DisposableBean {
    /**
     * DisposableBean 提供了bean销毁的方法
     * 凡是在继承此接口的bean被销毁时，都会调用此方法
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        log.warn("DisposableBean"+toString());
    }

    /**
     * InitializingBean 提供了bean的初始化方式。
     * 凡是在继承此接口的bean初始化时 都会调用此方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("InitializingBean"+toString());
    }


}
