package com.hp.placeholder.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
@EnableSwagger2
public class SwaggerAutoConfigration {

    public static final String SWAGGER_PACKAGE="com.hp.placeholder";

    public static final String VERSION="1.0.0";

    @Bean
    public Docket createDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_PACKAGE))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("接口文档").description("hp").version(VERSION).contact("韩鹏").build();
    }


}
