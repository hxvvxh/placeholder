package com.hp.placeholder;

import com.hp.placeholder.imports.IntImportBeanDefinitionRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages = {
        "com.hp.placeholder"
},exclude = DataSourceAutoConfiguration.class)
/**
 * import1 直接在主类上添加
 */
//@Import(Color.class)
/**
 * import2 使用自定义的importSerimportSelector
 */
//@Import(ColorImportSelector.class)
/**
 * improt3 使用ImportBeanDefinitionRegistrar(手工定义bean)
 */
@Import({IntImportBeanDefinitionRegistrar.class})
/**
 * 此注解表示可以使用@ConfigurationProperties
 */
@EnableConfigurationProperties
public class PlaceholderApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PlaceholderApplication.class);
        ApplicationContext context = application.run(args);

        Arrays.asList(context.getBeanDefinitionNames()).forEach(c -> {
            System.out.println(context.getBean(c).getClass().getSimpleName());
        });
//		SpringApplication.run(PlaceholderApplication.class, args);
        System.out.println("server starter up with" + System.currentTimeMillis());
    }
}
