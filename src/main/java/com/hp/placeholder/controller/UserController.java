package com.hp.placeholder.controller;

import com.hp.placeholder.applicationContext.HpApplicationContext;
import com.hp.placeholder.listener.my.MyApplicationEvent;
import com.hp.placeholder.postprocess.bean.HpBeanDefin;
import com.hp.placeholder.wxb.aop.annotion.Log;
import com.hp.placeholder.wxb.bean.Red;
import com.hp.placeholder.wxb.pojo.LiftCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hp
 * @version 1.0
 * @date 2020/5/6 19:54
 */
@RestController
public class UserController {
    @Value("${hp.value}")
    private String value;
    @Value("?{hp.placeholder}")
    private String placeholder;
    @Value("${hp.serverPort}")
    private String serverPort;
    @Autowired
    private Red red;
    @GetMapping(value = "/value")
    public String getValue(){
        return value;
    }
    @GetMapping(value = "/placeHolder")
    public String getHpValue(){
        return placeholder;
    }
    @GetMapping(value = "/serverPort")
    public String getServerPort(){
        return serverPort;
    }
    @GetMapping(value = "/red",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRed(){
        return red.getRed();
    }

    @GetMapping(value = "/name",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRedName(){
        return red.getName();
    }

    @GetMapping(value = "/applicationName",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRedApplicationName(){
        return red.getApplicationName();
    }

    @GetMapping(value = "/hp/{hp}")
    @Log(value = "hppp",log = "UserController")
    public String hp(@PathVariable("hp") String hp){
        return hp+"test";
    }

    @Autowired
    private HpApplicationContext hpApplicationContext;

    @Autowired
    private LiftCycle liftCycle;

    /**
     * 验证自定义事件监听
     * 使用context.publishEvent(ApplicationEvent)
     */
    @GetMapping(value = "/getListener",produces = MediaType.APPLICATION_JSON_VALUE)
    public void getListener(){
        MyApplicationEvent myApplicationEvent=new MyApplicationEvent(liftCycle);
        hpApplicationContext.getContext().publishEvent(myApplicationEvent);
    }

    @Autowired(required = false)
    private HpBeanDefin definOfBeanFactory;

    @GetMapping(value = "/getBeanDefinOfBeanFactory",produces = MediaType.APPLICATION_JSON_VALUE)
    public HpBeanDefin getBeanDefinOfBeanFactory(){
        return definOfBeanFactory;
    }
}
