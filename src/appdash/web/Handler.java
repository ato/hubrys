package appdash.web;

public interface Handler {
    Response handle(Request request) throws Exception;
}
