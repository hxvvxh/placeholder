# placeholder
# hp的springboot相关学习
    
    原有功能：
        (1) springboot 启动端口改成在数据库表中配置项(placeholder库中spring表中serverPort字段值：本项目为9292)
        (2) @Value("${hp.placeholder}") 修改为自定义的@Value("?{hp.placeholder}") 实现属性注入
        (3) application-dev.yml 中 属性值 使用 ?{hp.place} 替代 ${hp.place} 从 VM option 中获取-Dhp.place=placeHolder 属性值
        
    后功能：
        (1)sprinboot 学习知识点代码
    
    
    
    本地启动：
        (1)数据库中增加placeholder库,spring表,serverPort列 ，初始化一条数据 serverPort=9292
        (2)修改com.hp.placeholder.Initalizer.HpApplicationContextInitalizer.getUser 中数据库连接信息(因为重点不在数据库表获取，所以这里使用了原生的JDBC)
        (3)启动PlaceholderApplication前，请在 VM option 中添加-Dhp.place=placeHolder 值可自定义
        (4)启动
        (5)访问 http://localhost:9292/placeHolder
    
    swagger2:
        (1)项目集成了swagger2 可视化接口文档
            项目启动后访问：访问http://localhost:9292/swagger-ui.html 
            可显示出所有的接口，也可调用测试。   
