package com.hp.placeholder.Initalizer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hp
 * @version 1.0
 * @date 2020/5/15 16:29
 */
public class HpApplicationContextInitalizer implements ApplicationContextInitializer,Ordered {

    private int ordered=Ordered.HIGHEST_PRECEDENCE;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment=applicationContext.getEnvironment();
        MutablePropertySources propertySources=environment.getPropertySources();
        Spring spring=getUser();
        if (Objects.isNull(spring)){
            return;
        }
        Map<String,Object> map=new HashMap<>();
        //这里可以赋值多个配置属性
        map.put("hp.serverPort",spring.getServerPort());
        map.put("server.port",Integer.valueOf(spring.getServerPort()));
        PropertySource propertySource=new SystemEnvironmentPropertySource("hp",map);
        propertySources.addFirst(propertySource);
    }

    @Override
    public int getOrder() {
        return ordered;
    }


    @Data
    @AllArgsConstructor
    private class Spring{
        private String serverPort;
    }

    private Spring getUser(){
        try {
            Connection connection=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/placeholder?useUnicode=true&serverTimezone=UTC&characterEncoding=utf8", "root", "hanpeng3510068");
            String sql="SELECT * FROM spring";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                String s=resultSet.getString("serverPort");
                return new Spring(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
