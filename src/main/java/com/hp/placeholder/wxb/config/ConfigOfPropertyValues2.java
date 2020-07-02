package com.hp.placeholder.wxb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ConfigurationProperties 这种加载属性资源的配置  需要 属性在主配置文件中存在（感觉不对）
 * 就是spring在启动时加载到的资源文件的地方
 *
 * @EnableConfigurationProperties 可以不t添加在主类上  但是前提条件是
 * (1) @Scan@omponent 可以扫描到的包中
 * (2)@Component 注册到spring ioc容器中
 * 说白了就是 需要spring能管理到的bean
 * */
@Data
@Component
@ConfigurationProperties(prefix = "hpp",ignoreInvalidFields = true)
//@EnableConfigurationProperties
public class ConfigOfPropertyValues2 {
    private String userName;
    private String passWord;
}
