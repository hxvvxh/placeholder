package com.hp.placeholder.placeholder;

import org.springframework.core.env.PropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertyPlaceholderHelper;

public class HpPropertySourcesPropertyResolver extends PropertySourcesPropertyResolver {
    /**
     * 自定义的占位符策略
     */
    protected String placeholderPrefix="?{";
    protected String placeholderSuffix = "}";
    protected String valueSeparator =":";
    @Nullable
    private final PropertySources propertySources;
    protected boolean ignoreUnresolvablePlaceholders = false;
    @Nullable
    private PropertyPlaceholderHelper strictHelper;
    /**
     * Create a new resolver against the given property sources.
     */
    HpPropertySourcesPropertyResolver(PropertySources propertySources) {
        super(propertySources);
        this.propertySources=propertySources;
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = createPlaceholderHelper(false);
        }
        return doResolvePlaceholders(text, this.strictHelper);
    }

    private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix,
                this.valueSeparator, ignoreUnresolvablePlaceholders);
    }

    private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, this::getPropertyAsRawString);
    }
}
