package appdash.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public interface Response {
    void send(Request request) throws Exception;

    static Response seeOther(String path) {
        return request -> {
            HttpServerExchange exchange = request.exchange();
            exchange.setStatusCode(303);
            exchange.getResponseHeaders().add(Headers.LOCATION, path);
        };
    }
}
