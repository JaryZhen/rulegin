package tb.rulegin.server.common.component;

public interface ConfigurableComponent<T> {

    void run(T configuration);

}
