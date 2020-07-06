package com.hp.placeholder.wxb.pojo;

import lombok.Data;

@Data
public class LiftCycle {

    private String name;



    public void init(){
        this.name="liftCycle";
    }

    public void destroy(){
        this.name=null;
    }
}
