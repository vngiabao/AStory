package com.ph.core.story.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.query")
public class QueryProperties {

    private int maxPageSize;
    private int maxInSize;
    private int maxDepth;
    private int maxFilters;
}

