package com.hp.placeholder.listener.my;

import org.springframework.context.ApplicationEvent;

/**
 * 自定义ApplicationEvent事件
 */
public class MyApplicationEvent extends ApplicationEvent {
    public MyApplicationEvent(Object source) {
        super(source);
    }
}
