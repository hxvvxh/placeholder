package com.hp.placeholder.wxb.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 加载自定义属性文件内容1
 * @PropertySource()
 * @Value
 * @Component
 *联合使用  获取资源文件中特定的属性值
 * 但是这种spring官方是不推荐此方法的  因为在刷新的过程中加载的时机太晚
 */
@Data
@Component
@PropertySource(value = {"classpath:hp.yml"})
public class ConfigOfPropertyValues {
    @Value("${hp}")
    private String hp;
    private String userName;
    private String passWord;
}
