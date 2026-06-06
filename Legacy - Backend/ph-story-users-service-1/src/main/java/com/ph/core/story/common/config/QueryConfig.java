package com.ph.core.story.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ph.core.lib.config.QuerySecurityConfig;

@Configuration
public class QueryConfig {

    @Bean
    public QuerySecurityConfig querySecurityConfig(QueryProperties props) {
        return new QuerySecurityConfig(props.getMaxPageSize(), props.getMaxInSize(),
                props.getMaxDepth(), props.getMaxFilters());
    }
}
