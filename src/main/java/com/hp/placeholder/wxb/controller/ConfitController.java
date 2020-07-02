package com.hp.placeholder.wxb.controller;

import com.hp.placeholder.wxb.bean.Red;
import com.hp.placeholder.wxb.config.ConfigOfPropertyValues;
import com.hp.placeholder.wxb.config.ConfigOfPropertyValues2;
import com.hp.placeholder.wxb.pojo.Auto;
import com.hp.placeholder.wxb.pojo.AutoOfOther;
import com.hp.placeholder.wxb.pojo.PojoInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ResourceBundle;


@RestController
public class ConfitController {

    /**
     * @Autowired 第一种 属性上
     *  直接从ioc容器中获取参数组件的值
     *  @Autowired：beanFactory.getBean(XXX.class) 是使用类型匹配的
     */
    @Autowired
    private Auto auto;

    private AutoOfOther autoOfOther;

    private Red red;

    private String test;

    /**
     * @Autowired @see ConfigOfAutoConfiguration
     *@Autowired  属性上
     * 1.1 当spring ioc 容器中只有一个实例的时候(单例) 直接使用是没有问题的
     * 当ioc容器中存在多个的时候(多例)时
     * 使用 @Primary 指定spring 让Spring进行自动装配的时候，默认使用首选的bean
     * 就可以单独使用@Autowired了
     */
    @Autowired
    private PojoInt pojoInt1;
    /**
     *@Autowired 属性上
     * 1.2 spring ioc容器多个实例时
     * 可以使用 @Qualifier(value = "pojoIntImpl2") 从spring ioc容器中获取指定id的bean 去进行注入
     * @Qualifier 需要与@Autowired 一起使用，在使用了@Primary 不会影响@Qualifier的使用
     * @Qualifier beanFactory.getBean("xxx") 是使用id去匹配的 id就是在spring中注册的类名
     */
    @Autowired
    @Qualifier(value = "pojoIntImpl2")
    private PojoInt pojoInt2;

    /**
     * @Resource
     * 和@Autowired一样实现自动装配功能。单独使用！！！
     * 默认是按照组件名称进行装配的
     * 注意  是按照名称去匹配  而不是按照名称去匹配
     * spring在自动装配时，如果不是显示的指定名称@Component(value = "XXX") 默认是使用了名称作为id的
     * 也就是说 存在 spring ioc 容器中 id和name是同一个的情况(大部分是这样)
     */
    @Resource(name = "pojoIntImpl3")
    private PojoInt pojoInt3;


    /**
     * @Autowired 第二种 构造方法上
     * 默认加载ioc容器中的组件，容器启动时会调用无参的构造器创建对象，在进行属性的赋值
     * @Autowired 标注在构造器上可以默认的调用该方法，方法中的入参属性同样是从ioc容器中获取的（这个和@bean在方法上是一样的）
     * 这里需要注意的点是：如果容器中只有一个有参的构造器的话，这个有参的构造器是可以省略@Autowired,参数还是ioc容器中获取的
     */

    /**
     * 下面的这三个构造器 验证了上述--可依次注掉验证
     * (1) 当只有一个有参的构造器时，不需要@Autowired 即可实现属性的注入
     * (2) 当存在多个有参的构造器时，使用带有@Autowired 的实现属性注入
     * (3) 当只有有参的构造器时（多个），且都没有使用@Autowired 注解时，会启动报错
     *     验证了 spring是使用了无参的构造器去创建bean，然后再去为属性赋值的
     *
     *     调用：newInstance0() 构造器 去实例化bean的
     * @param autoOfOther
     */
    @Autowired
    public ConfitController(AutoOfOther autoOfOther) {
        this.autoOfOther = autoOfOther;
    }

    public ConfitController(AutoOfOther autoOfOther, String test) {
        this.autoOfOther = autoOfOther;
        this.test = test;
    }

    public ConfitController() {
    }


    /**
     *@Autowired  第三种 方法上
     *  方法上 在spring初始化bean的时候就会调用此方法 入参依赖是从ioc容器中获取的
     *
     *  调用  inject()->invoke() 代理方法去执行赋值的
     * @param red
     */
    @Autowired
    private void autoRed(Red red){
        this.red=red;
    }


    @GetMapping(value = "/getAuto",produces = MediaType.APPLICATION_JSON_VALUE)
    private Auto getAuto(){
        return auto;
    }
    @GetMapping(value = "/getAutoOfOther",produces = MediaType.APPLICATION_JSON_VALUE)
    private AutoOfOther getAutoOfOther(){
        return autoOfOther;
    }
    @GetMapping(value = "/getRedOfMethod",produces = MediaType.APPLICATION_JSON_VALUE)
    private Red getRed(){
        return red;
    }

    @GetMapping(value = "/getPojoInt",produces = MediaType.APPLICATION_JSON_VALUE)
    private String getPojoInt1(){
        return pojoInt1.getInt();
    }

    @GetMapping(value = "/getPojoInt1",produces = MediaType.APPLICATION_JSON_VALUE)
    private String getPojoInt2(){
        return pojoInt2.getInt();
    }

    @GetMapping(value = "/getPojoInt2",produces = MediaType.APPLICATION_JSON_VALUE)
    private String getPojoInt3(){
        return pojoInt3.getInt();
    }

    @Autowired
    private ConfigOfPropertyValues configOfPropertyValues;

    @GetMapping(value = "/getPropertyValues",produces = MediaType.APPLICATION_JSON_VALUE)
    private ConfigOfPropertyValues getPropertyValues(){
        return configOfPropertyValues;
    }
    @Autowired
    private ConfigOfPropertyValues2 configOfPropertyValues2;

    @GetMapping(value = "/getPropertyValues2",produces = MediaType.APPLICATION_JSON_VALUE)
    private ConfigOfPropertyValues2 getPropertyValues2(){
        return configOfPropertyValues2;
    }


    @GetMapping(value = "/getPropertyValues3",produces = MediaType.APPLICATION_JSON_VALUE)
    private String getPropertyValues3(){
        return ResourceBundle.getBundle("hpp").getString("hp.test");
    }
}
