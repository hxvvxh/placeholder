package com.hp.placeholder.placeholder.autoConfig;

import com.hp.placeholder.placeholder.HpPropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author hp
 * @version 1.0
 * @date 2020/5/11 19:55
 */

/**
 * 给容器注入组件
 * 1：包扫描+ 组件注解（@Component、@Service、@Controller、@Repository）
 * 2:@Bean 导入第三方包里的组件
 * 3：@Import 快速给容器导入一个组件
 */
@Configuration
public class HpPlaceHolderAutoConfigurer {
    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public static HpPropertyPlaceholderConfigurer hpPropertyPlaceholderConfigurer(){
        return new HpPropertyPlaceholderConfigurer();
    }

    /**
     * 使用Condition 对java运行环境进行判断注入bean
     * 单实例bean：默认在容器启动时创建对象
     *
     * @return
     */
    @Bean
    @ConditionalOnExpression(value = "'${spring.profiles.active}'.equals('dev')")
    public Condition buildCondition(){
        //适配linux系统
        Condition condition= (context, metadata) -> {
            ConfigurableListableBeanFactory factory=context.getBeanFactory();
            ClassLoader classLoader=context.getClassLoader();
            Environment environment=context.getEnvironment();
            BeanDefinitionRegistry  registry=context.getRegistry();

            String property=environment.getProperty("os.name");
            if (property.contains("Linux")){
                return true;
            }
            return false;
        };
        //适配windows系统
        Condition conditionWindows =(context, metadata) -> {
            ConfigurableListableBeanFactory factory=context.getBeanFactory();
            ClassLoader classLoader=context.getClassLoader();
            Environment environment=context.getEnvironment();
            BeanDefinitionRegistry  registry=context.getRegistry();

            String property=environment.getProperty("os.name");
            if (property.contains("Windows")){
                return true;
            }
            return false;
        };
        return condition;
    }
}
