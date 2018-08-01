package hubrys.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.PathTemplateMatch;

public class Request {
    private final HttpServerExchange exchange;

    public Request(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    public HttpServerExchange exchange() {
        return exchange;
    }

    public String path(String param) {
        String value = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY).getParameters().get(param);
        if (value == null) {
            throw new IllegalArgumentException(param);
        }
        return value;
    }
}
