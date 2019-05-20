package com.github.rulegin.core.datasource.conf;

import lombok.Data;

@Data
public class KafkaSingleConfiguration {
    private boolean sync;
    private String topic;
    private String format;
    private String[] keys;

    private final String condition;
}
