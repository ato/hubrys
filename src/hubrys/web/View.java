package hubrys.web;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class View implements Response {
    private static Configuration freemarker;

    static {
        freemarker = new Configuration(Configuration.VERSION_2_3_28);
        freemarker.setDefaultEncoding("UTF-8");
        freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        freemarker.setClassForTemplateLoading(View.class, "/");
        freemarker.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        freemarker.setOutputFormat(HTMLOutputFormat.INSTANCE);
    }

    private final String name;
    private final Map<String, Object> model = new HashMap<>();

    public View(String name) {
        this.name = name;
    }

    public View put(String name, Object value) {
        model.put(name, value);
        return this;
    }

    @Override
    public void send(Request request) throws IOException, TemplateException {
        HttpServerExchange exchange = request.exchange();
        model.put("request", request);
        StringWriter buffer = new StringWriter();
        Template template = freemarker.getTemplate(name);
        template.process(model, buffer);
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html");
        exchange.getResponseSender().send(buffer.toString());
    }
}
