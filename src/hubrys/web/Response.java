package hubrys.web;

import io.undertow.io.DefaultIoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.xnio.IoUtils;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface Response {
    void send(Request request) throws Exception;

    static Response sendFile(FileChannel channel) {
        return request -> {
            HttpServerExchange exchange = request.exchange();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().transferFrom(channel, new DefaultIoCallback() {
                @Override
                public void onComplete(HttpServerExchange exchange, Sender sender) {
                    IoUtils.safeClose(channel);
                    super.onComplete(exchange, sender);
                }

                @Override
                public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
                    IoUtils.safeClose(channel);
                    super.onException(exchange, sender, exception);
                }
            });};
    }

    static Response seeOther(String path) {
        return request -> {
            HttpServerExchange exchange = request.exchange();
            exchange.setStatusCode(303);
            exchange.getResponseHeaders().add(Headers.LOCATION, path);
        };
    }
}
