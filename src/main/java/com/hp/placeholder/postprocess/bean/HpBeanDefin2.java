package com.hp.placeholder.postprocess.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HpBeanDefin2 {

    private String name;
    private String pass;
    private String hp;
    private String ref;
    private HpBeanDefin hpBeanDefin;
//    private HpBeanDefin3 hpBea;

    public HpBeanDefin2 get(){
        return this;
    }

    private void init(){
        this.name="name";
        this.pass="pass";
        this.hp="hpBeanDefin2";
    }
}
