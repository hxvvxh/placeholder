package com.hp.placeholder.wxb.pojo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class PojoIntImpl1 implements PojoInt {
    @Override
    public String getInt() {
        return "pojoIntImpl1";
    }
}
