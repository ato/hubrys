package hubrys.web;

import io.undertow.server.HttpServerExchange;

public interface Response {
    void send(HttpServerExchange exchange) throws Exception;
}
