package hubrys.web;

import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.pac4j.core.exception.TechnicalException;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandler implements HttpHandler {
    private final HttpHandler next;

    public ErrorHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.addDefaultResponseListener(exchange1 -> {
            if (!exchange.isResponseChannelAvailable()) {
                return false;
            }
            switch (exchange.getStatusCode()) {
                case 401:
                    exchange.getResponseSender().send("Authentication required");
                    return true;
                case 403:
                    exchange.getResponseSender().send("Your account does not have access.");
                    return true;
                case 500:
                    Throwable t = exchange.getAttachment(DefaultResponseListener.EXCEPTION);
                    if (t instanceof TechnicalException && t.getCause() != null) {
                        t = t.getCause(); // remove pac4j's wrapper
                    }
                    StringWriter sw = new StringWriter();
                    t.printStackTrace(new PrintWriter(sw));
                    exchange.getResponseSender().send(sw.toString());
                    return true;
            }
            return false;
        });
        next.handleRequest(exchange);
    }
}
