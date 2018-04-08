package cn.neucloud.server.core.filter;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.extern.slf4j.Slf4j;

import javax.script.*;
import java.util.Map;

/**
 * Created by Jary on 2017/12/22 0022.
 */
@Slf4j
public class SingleJsEvaluator implements JsEvaluator {


    private static NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    private CompiledScript engine;

    //typeof temperature !== 'undefined' && temperature >= 100
    public SingleJsEvaluator(String script) {
        engine = compileScript(script);
    }

    private static CompiledScript compileScript(String script) {
        ScriptEngine engine = factory.getScriptEngine(new String[]{"--no-java"});
        Compilable compEngine = (Compilable) engine;
        try {
            return compEngine.compile(script);
        } catch (ScriptException e) {
            log.warn("Failed to compile filter script: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Can't compile script: " + e.getMessage());
        }
    }


    public static Bindings toBindings(Map<String, Integer> map) {
        return toBindings(new SimpleBindings(), map);
    }

    public static Bindings toBindings(Bindings bindings, Map<String, Integer> map) {

        bindings.putAll(map);
        return bindings;
    }

    @Override
    public void execute(Bindings bindings) throws ScriptException {
        Object eval = engine.eval(bindings);
        System.out.println("Result: " + eval);
    }

    @Override
    public void execute(String script, Bindings bindings) throws ScriptException {

    }

    public void destroy() {
        engine = null;
    }
}
