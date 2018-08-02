package hubrys;

import hubrys.web.NotFoundException;
import hubrys.web.Request;
import hubrys.web.Router;
import hubrys.web.View;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Hubrys {

    private String removeSuffix(String s, String suffix) {
        return s.endsWith(suffix) ? s.substring(0, s.lastIndexOf(suffix)) : s;
    }

    private List<String> listApps() throws IOException {
        return Files.list(Paths.get("/etc/jvmctl/apps"))
                .map(p -> p.getFileName().toString())
                .filter(s -> s.endsWith(".conf"))
                .map(s -> s.substring(0, s.length() - ".conf".length()))
                .sorted()
                .collect(Collectors.toList());
    }

    void run() {
        Router router = new Router("hubrys/static");
        router.resources("/webjars", "META-INF/resources/webjars");
        router.GET("/", this::index);
        router.GET("/apps/{app}", this::app);
        router.GET("/apps/{app}/config", this::config);
        router.GET("/apps/{app}/logs", this::logs);

        HttpHandler handler = new BlockingHandler(router);
        Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build().start();
    }


    private View app(Request request) throws Exception {
        return view("app").put("app", App.get(request.path("app")));
    }

    private View logs(Request request) throws Exception {
        return view("logs").put("app", App.get(request.path("app")));
    }

    private View config(Request request) throws Exception {
        return view("config").put("app", App.get(request.path("app")));
    }

    private View index(Request request) throws Exception {
        return view("index");
    }

    public static void main(String args[]) {
        new Hubrys().run();
    }

    private View view(String name) throws IOException {
        return new View("/hubrys/" + name + ".ftlh")
                .put("apps", listApps());
    }

}
