package cn.neucloud.server.core.datasource.conf;

import lombok.Data;

@Data
public class KafkaStreamConfiguration {
    private boolean sync;
    private String topic;
    private String format;
    private String[] keys;
    private Integer size;
    private Integer step;

    private final String condition;
}
