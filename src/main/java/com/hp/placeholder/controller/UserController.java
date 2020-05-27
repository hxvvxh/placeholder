package com.hp.placeholder.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
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
}
