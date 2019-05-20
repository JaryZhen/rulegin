package com.github.rulegin.core;

/**
 * Created by Jary on 2018/1/23 0023.
 */
public class RuleConstants {
    private RuleConstants(){
    }

    public static final String TYPE = "type";
    public static final String CONDITION = "condition";
    public static final String CONFIGURATION = "configuration";

    //datasource
    public static final String DATASOURCE_TYPE_KAFKA="kafka";
    public static final String DATASOURCE_TYPE_SPARK="spark";

    //filter
    public static final String FILTERS_TYPE_SINGLE="single";
    public static final String FILTERS_TYPE_KAFKAWINDOW="kafkaWindow";
    public static final String FILTERS_TYPE_SPARKWINDOW="spark_window";

    // window
    public static final String WINDOW_SIZE="size";
    public static final String WINDOW_STEP="step";





}
