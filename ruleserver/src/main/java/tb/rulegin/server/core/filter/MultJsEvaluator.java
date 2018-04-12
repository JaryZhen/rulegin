package tb.rulegin.server.core.filter;

import tb.rulegin.server.utils.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/**
 * Created by Jary on 2017/12/22 0022.
 */
@Slf4j
public class MultJsEvaluator implements JsEvaluator{

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    static ScriptContext defaultCtx;

    public MultJsEvaluator() {
        try {
            engine.eval(new FileReader(ResourceUtil.getJS("BasisFuc.js")));
            defaultCtx = engine.getContext();

        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Bindings toBindings(Map<String, Integer> map) {

        Bindings bi = defaultCtx.getBindings(ScriptContext.ENGINE_SCOPE);
        bi.putAll(map);
        return  bi;
    }

    @Override
    public void execute(Bindings bindings) throws ScriptException {
        Object result = engine.eval("Sum(a,b)", bindings);
        System.out.println("result: "+result);

    }

    /*
    * Bindings bi = defaultCtx.getBindings(ScriptContext.ENGINE_SCOPE);

            bi.put("a", 1);
            bi.put("b", 2);
            bi.put("a", 2);

            Object result2 = null;//Sum(p1,P2) > 23 Sum(p1+P2) > 5

            result2 = engine.eval("Sum(a,b)", bi);

            System.out.println(result2);
    *
    * */
    @Override
    public void execute(String script, Bindings bindings) throws ScriptException {
        Object result = engine.eval(script, bindings);
        if((Boolean) result) {
            System.out.println("High temperature: " + result);
        }
    }

    public void destroy() {
        engine = null;
    }
}
