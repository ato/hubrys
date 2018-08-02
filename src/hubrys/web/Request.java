package hubrys.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.PathTemplateMatch;
import org.pac4j.undertow.account.Pac4jAccount;

import java.util.Base64;

public class Request {
    private final HttpServerExchange exchange;
    private final String FLASH_COOKIE = "appdash-flash";

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

    public String formValue(String field) {
        return exchange.getAttachment(FormDataParser.FORM_DATA).getFirst(field).getValue();
    }

    public Pac4jAccount account() {
        return (Pac4jAccount) exchange.getSecurityContext().getAuthenticatedAccount();
    }

    public void flash(String message) {
        String encoded = message == null ? null : Base64.getUrlEncoder().encodeToString(message.getBytes());
        Cookie cookie = new CookieImpl(FLASH_COOKIE, encoded)
                .setHttpOnly(true)
                .setPath("/")
                .setSameSiteMode("Lax");
        exchange.setResponseCookie(cookie);
    }

    public String flash() {
        Cookie cookie = exchange.getRequestCookies().get(FLASH_COOKIE);
        flash(null);
        return cookie == null ? null : new String(Base64.getUrlDecoder().decode(cookie.getValue()));
    }
}
