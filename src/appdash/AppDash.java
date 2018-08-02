package appdash;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import appdash.backend.App;
import appdash.web.*;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.undertow.handler.CallbackHandler;
import org.pac4j.undertow.handler.LogoutHandler;
import org.pac4j.undertow.handler.SecurityHandler;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AppDash {

    private List<String> listApps() throws IOException {
        return Files.list(Paths.get("/etc/jvmctl/apps"))
                .map(p -> p.getFileName().toString())
                .filter(s -> s.endsWith(".conf"))
                .map(s -> s.substring(0, s.length() - ".conf".length()))
                .sorted()
                .collect(Collectors.toList());
    }

    void run() {
        Config authConfig = initAuthConfig();

        Router router = new Router("appdash/static");
        router.resources("/webjars", "META-INF/resources/webjars");
        router.GET("/", this::index);
        router.GET("/apps/{app}", this::overview);
        router.GET("/apps/{app}/config", this::config);
        router.POST("/apps/{app}/config", this::saveConfig);
        router.POST("/apps/{app}/restart", this::restart);
        router.POST("/apps/{app}/stop", this::stop);
        router.GET("/apps/{app}/logs", this::logs);
        router.GET("/apps/{app}/logs/stdio.log", this::stdioLog);

        HttpHandler handler = new BlockingHandler(router);
        handler = new EagerFormParsingHandler(handler);
        handler = SecurityHandler.build(handler, authConfig, "OidcClient", "developer");
        handler = Handlers.path(handler)
                .addExactPath("/logout", new LogoutHandler(authConfig))
                .addExactPath("/callback", CallbackHandler.build(authConfig));
        handler = new SessionAttachmentHandler(handler,
                new InMemorySessionManager("appdash"),
                new SessionCookieConfig()
                        .setHttpOnly(true)
                        .setCookieName("appdash-session"));
        handler = new ErrorHandler(handler);

        Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build()
                .start();
    }

    private Response stdioLog(Request request) throws NotFoundException, IOException {
        App app = App.get(request.path("app"));
        try {
            FileChannel channel = FileChannel.open(app.stdioLog());
            long position = channel.size() - 128 * 1024;
            if (position > 0) {
                channel.position(position);
            }
            return Response.sendFile(channel);
        } catch (NoSuchFileException e) {
            throw new NotFoundException();
        }
    }

    private Response stop(Request request) throws Exception {
        App app = App.get(request.path("app"));
        request.flash(app.stop());
        return seeOverview(app);
    }

    private Response restart(Request request) throws Exception {
        App app = App.get(request.path("app"));
        request.flash(app.restart());
        return seeOverview(app);
    }

    private Config initAuthConfig() {
        OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId(System.getenv("OIDC_CLIENT_ID"));
        oidcConfiguration.setSecret(System.getenv("OIDC_SECRET"));
        oidcConfiguration.setDiscoveryURI(System.getenv("OIDC_URL"));
        oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        OidcClient<OidcProfile> oidcClient = new OidcClient<>(oidcConfiguration);
        oidcClient.setAuthorizationGenerator((context, profile) -> {profile.addRole("developer"); return profile;});

        Clients clients = new Clients("http://localhost:8080/callback", oidcClient);
        Config config = new Config(clients);
        config.addAuthorizer("developer", new RequireAnyRoleAuthorizer("developer"));
        return config;
    }

    private Response saveConfig(Request request) throws Exception {
        App app = App.get(request.path("app"));
        CommonProfile profile = request.account().getProfile();
        String config = request.formValue("config").replace("\r\n", "\n");
        if (app.config().equals(config)) {
            request.flash("Configuration unmodified.");
        } else {
            app.saveConfig(config, profile.getDisplayName(), profile.getEmail());
            request.flash("Configuration updated. Please restart or deploy.");
        }
        return seeOverview(app);
    }

    private Response seeOverview(App app) {
        return Response.seeOther("/apps/" + app.name());
    }

    private View overview(Request request) throws Exception {
        return view("overview").put("app", App.get(request.path("app")));
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
        new AppDash().run();
    }

    private View view(String name) throws IOException {
        return new View("/appdash/" + name + ".ftlh")
                .put("apps", listApps());
    }

}
