package com.hp.placeholder.imports.impl;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
@Service
public class Int1 implements Int {
    private String version;
    @Override
    public String version() {
        return version;
    }

    @PostConstruct
    public void init(){
        this.version="Int1";
    }
}
