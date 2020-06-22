package com.hp.placeholder.wxb.bean;

import com.hp.placeholder.imports.Color;
import com.hp.placeholder.wxb.bean.base.BaseService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarService extends BaseService {
    @Autowired
    private Color color;


    @Override
    public String toString() {
        return "CarService{" +
                "color=" + color +
                '}';
    }
}
