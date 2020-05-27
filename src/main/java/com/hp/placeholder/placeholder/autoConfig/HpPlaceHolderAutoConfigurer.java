package com.hp.placeholder.placeholder.autoConfig;

import com.hp.placeholder.placeholder.HpPropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hp
 * @version 1.0
 * @date 2020/5/11 19:55
 */
@Configuration
public class HpPlaceHolderAutoConfigurer {
    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public static HpPropertyPlaceholderConfigurer hpPropertyPlaceholderConfigurer(){
        return new HpPropertyPlaceholderConfigurer();
    }
}
