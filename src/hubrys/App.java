package hubrys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class App {
    private final String name;

    public static Optional<App> get(String name) {
        if (!Files.exists(Paths.get("/etc/jvmctl/apps/" + name + ".conf"))) {
            return Optional.empty();
        }
        return Optional.of(new App(name));
    }

    private App(String name) {
        this.name = name;
    }

    private Path dir() {
        return Paths.get("/apps/" + name);
    }

    public String version() {
        try {
            Path gitFile = dir().resolve("git-revision");
            if (Files.exists(gitFile)) {
                for (String line : Files.readAllLines(gitFile)) {
                    if (line.startsWith("refs/tags/")) {
                        return line.substring("refs/tags/".length());
                    } else if (line.startsWith("commit ")) {
                        return line.substring("commit ".length(), "commit ".length() + 7);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }
}
