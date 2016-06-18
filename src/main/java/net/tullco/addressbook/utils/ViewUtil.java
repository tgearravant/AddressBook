package net.tullco.addressbook.utils;


import java.util.Map;

import net.tullco.addressbook.utils.Path;
import org.apache.velocity.app.*;
import spark.template.velocity.*;
import spark.ModelAndView;
import spark.Request;

public class ViewUtil {
    public static String render(Request request, Map<String, Object> model, String templatePath) {
        model.put("WebPath", Path.Web.class); // Access application URLs from templates
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }
    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
}
