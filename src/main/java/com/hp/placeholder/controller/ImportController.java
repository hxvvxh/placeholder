package com.hp.placeholder.controller;

import com.hp.placeholder.imports.Color;
import com.hp.placeholder.imports.impl.Int;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ImportController {
    @Autowired
    private Color color;

    /**
     *    @Autowired
     *    @Qualifier("int1")
     *    一起使用
     */
    @Autowired
    @Qualifier("int1")
    private Int int1;

    /**
     *@Resource 是根据name去匹配的
     */
    @Resource
    private Int int2;
    @GetMapping(value = "/color")
    public Color getColor(){
        return color;
    }
    @GetMapping(value = "/int1")
    public String getInt1Color(){
        return int1.version();
    }
    @GetMapping(value = "/int2")
    public String getInt2Color(){
        return int2.version();
    }
}
