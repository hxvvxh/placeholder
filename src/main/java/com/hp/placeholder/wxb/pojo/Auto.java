package com.hp.placeholder.wxb.pojo;

import lombok.Data;

import javax.annotation.PostConstruct;

@Data
public class Auto {
    private String name;

    @PostConstruct
    public void init(){
        this.name="auto";
    }
}
