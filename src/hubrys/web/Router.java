package hubrys.web;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.util.Headers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import static io.undertow.Handlers.resource;

public class Router implements HttpHandler {
    private final RoutingHandler routing;
    private final PathHandler pathHandler;

    public Router(String resourcePath) {
        pathHandler = Handlers.path(Handlers.resource(new ClassPathResourceManager(getClass().getClassLoader(), resourcePath)));;
        routing = Handlers.routing(false).setFallbackHandler(pathHandler);
    }

    public void GET(String pattern, Handler handler) {
        routing.get(pattern, exchange -> handler.handle(new Request(exchange)).send(exchange));
    }

    public void resources(String prefix, String resourcePath) {
        pathHandler.addPrefixPath(prefix, resource(new ClassPathResourceManager(getClass().getClassLoader(),
                resourcePath)).setCacheTime(60 * 60 * 24 * 365));
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            routing.handleRequest(exchange);
        } catch (NotFoundException e) {
            exchange.setStatusCode(404);
            exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("404 Not Found");
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.setStatusCode(500);
        }
    }
}
