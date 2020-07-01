package com.hp.placeholder.wxb.pojo;

import org.springframework.stereotype.Component;

@Component
public class PojoIntImpl2 implements PojoInt {
    @Override
    public String getInt() {
        return "pojoIntImpl2";
    }
}
