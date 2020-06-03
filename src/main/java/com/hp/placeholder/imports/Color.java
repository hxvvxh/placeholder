package com.hp.placeholder.imports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Color {
    private String color;

    @PostConstruct
    public void inits(){
        this.color="red";
    }
}
