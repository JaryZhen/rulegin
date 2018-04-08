package cn.neucloud.server.common.component;

public interface ConfigurableComponent<T> {

    void run(T configuration);

}
