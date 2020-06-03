package com.hp.placeholder.controller;

import com.hp.placeholder.factoryBean.MyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class factoryBeanController {
    @Autowired
    private MyFactoryBean.My my;

    @GetMapping("/my")
    public MyFactoryBean.My getMy(){
        return my;
    }
}
