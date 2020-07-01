package com.hp.placeholder.wxb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.PostConstruct;

@Data
public class AutoOfOther {
    private Auto auto;
    private String name;


    @PostConstruct
    private void init(){
        this.name="autoOfOther";
    }
}
