package tb.rulegin.server.core.filter;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Created by Jary on 2018/1/18 0018.
 */
public interface JsEvaluator {
    void execute(Bindings bindings) throws ScriptException;
    void execute(String script ,Bindings bindings) throws ScriptException;

}
