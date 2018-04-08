package cn.neucloud.server.utils;

import cn.neucloud.server.core.rule.RuleProcessingMetaData;
import cn.neucloud.server.common.data.kv.AttributeKvEntry;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class VelocityUtils {

    public static Template create(String source, String templateName) throws ParseException {
        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
        StringReader reader = new StringReader(source);
        SimpleNode node = runtimeServices.parse(reader, templateName);
        Template template = new Template();
        template.setRuntimeServices(runtimeServices);
        template.setData(node);
        template.initDocument();
        return template;
    }

    public static String merge(Template template, VelocityContext context)  {
        StringWriter writer = new StringWriter();
        try {
            template.merge(context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static VelocityContext createContext(RuleProcessingMetaData metadata) {
        VelocityContext context = new VelocityContext();
        metadata.getValues().forEach((k, v) -> context.put(k, v));
        return context;
    }


    private static void pushAttributes(VelocityContext context, Collection<AttributeKvEntry> deviceAttributes, String prefix) {
        Map<String, String> values = new HashMap<>();
        deviceAttributes.forEach(v -> values.put(v.getKey(), v.getValueAsString()));
        context.put(prefix, values);
    }
}
