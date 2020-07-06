package com.hp.placeholder.listener.my;

import com.hp.placeholder.wxb.pojo.LiftCycle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 自定义ApplicationListener，监听自定义事件MyApplicationEvent
 */
@Component
@Slf4j
public class MyApplicationListener implements ApplicationListener<MyApplicationEvent> {
    @Override
    public void onApplicationEvent(MyApplicationEvent myApplicationEvent) {
      log.info("MyApplicationEvent:{}",myApplicationEvent);
      LiftCycle liftCycle=LiftCycle.class.cast(myApplicationEvent.getSource());
      log.info("liftCycle"+liftCycle.getName());
    }
}
