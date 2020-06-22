package com.hp.placeholder.imports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Color {
    private String color;

    /**
     * 容器创建并为bean赋值后，调用此方法
     */
    @PostConstruct
    public void inits(){
        this.color="bu";
    }

    /**
     * 容器销毁之前
     */
    @PreDestroy
    public void destroy(){
        System.out.println("destroy color"+color);
    }
}
