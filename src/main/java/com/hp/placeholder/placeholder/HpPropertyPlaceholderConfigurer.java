package com.hp.placeholder.placeholder;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

import java.util.Properties;

import static org.springframework.context.support.PropertySourcesPlaceholderConfigurer.ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME;
import static org.springframework.context.support.PropertySourcesPlaceholderConfigurer.LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME;

/**
 * @author hp
 * @version 1.0
 * @date 2020/5/11 19:49
 * Spring 5.2 后
 */
public class HpPropertyPlaceholderConfigurer extends PlaceholderConfigurerSupport implements EnvironmentAware {

    @Nullable
    private PropertySources appliedPropertySources;
    @Nullable
    private Environment environment;
    @Nullable
    private MutablePropertySources propertySources;
    protected boolean ignoreUnresolvablePlaceholders = false;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertySources == null) {
            this.propertySources = new MutablePropertySources();
            if (this.environment != null) {
                this.propertySources.addLast(
                        new PropertySource<Environment>(ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME, this.environment) {
                            @Override
                            @Nullable
                            public String getProperty(String key) {
                                return this.source.getProperty(key);
                            }
                        }
                );
            }
            try {
                PropertySource<?> localPropertySource =
                        new PropertiesPropertySource(LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, mergeProperties());
                this.propertySources.addFirst(localPropertySource);
            } catch (Exception ex) {
                throw new BeanInitializationException("Could     not load properties", ex);
            }
        }
        processProperties(beanFactory, new HpPropertySourcesPropertyResolver(this.propertySources));
        this.appliedPropertySources = this.propertySources;
    }

    private void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                     final ConfigurablePropertyResolver propertyResolver) throws BeansException {

        StringValueResolver valueResolver = strVal -> {
            String resolved = (this.ignoreUnresolvablePlaceholders ?
                    propertyResolver.resolvePlaceholders(strVal) :
                    propertyResolver.resolveRequiredPlaceholders(strVal));
            if (this.trimValues) {
                resolved = resolved.trim();
            }
            return (resolved.equals(this.nullValue) ? null : resolved);
        };

        doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
    public void setPropertySources(PropertySources propertySources) {
        this.propertySources = new MutablePropertySources(propertySources);
    }

    /**
     * 弃用版本
     * @param beanFactory
     * @param props
     * @throws BeansException
     */
    @Deprecated
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        throw new UnsupportedOperationException(
                "Call processProperties(ConfigurableListableBeanFactory, ConfigurablePropertyResolver) instead");
    }
}
