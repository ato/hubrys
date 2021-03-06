package appdash.backend;

import appdash.web.NotFoundException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class App {
    private final String name;
    private long pid = -1;

    public static App get(String name) throws NotFoundException {
        if (!Files.exists(configFile(name))) {
            throw new NotFoundException();
        }
        return new App(name);
    }

    private App(String name) {
        this.name = name;
    }

    public String name() {
        return name;
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

    public String config() throws IOException {
        return String.join("\n", Files.readAllLines(configFile(name)));
    }

    private static Path configFile(String name) {
        return configBaseDir().resolve(name + ".conf");
    }

    private static Path configBaseDir() {
        return Paths.get("/etc/jvmctl/apps");
    }

    public void saveConfig(String config, String authorName, String authorEmail) throws IOException {
        Path file = configFile(name);
        Files.write(file, config.getBytes());

        ProcessBuilder git = new ProcessBuilder("git", "commit", "-o", "-m", "Changed " + name + " via Hubrys", file.toString());
        git.environment().put("GIT_COMMITTER_EMAIL", authorEmail);
        git.environment().put("GIT_COMMITTER_NAME", authorName);
        git.environment().put("GIT_AUTHOR_EMAIL", authorEmail);
        git.environment().put("GIT_AUTHOR_NAME", authorName);
        git.directory(configBaseDir().toFile());
        git.inheritIO();
        git.start();
    }

    public String restart() throws IOException, InterruptedException {
        return jvmctl("restart");
    }

    public String stop() throws IOException, InterruptedException {
        return jvmctl("stop");
    }

    private String jvmctl(String command) throws InterruptedException, IOException {
        File temp = File.createTempFile("appdash", ".stdio");
        try {
            new ProcessBuilder("jvmctl", command, name)
                    .redirectOutput(temp)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor(2, TimeUnit.MINUTES);
            return new String(Files.readAllBytes(temp.toPath()));
        } finally {
            temp.delete();
        }
    }

    public Path logPath(String log) {
        return Paths.get("/logs/" + name + "/" + log);
    }

    public long pid() throws IOException {
        if (pid != -1) {
            return pid;
        }
        Process p = new ProcessBuilder("systemctl", "show", "-p", "MainPID", "jvm:" + name)
                .inheritIO()
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = reader.readLine();
            if (line == null || !line.startsWith("MainPID=")) {
                throw new IOException("Unexpected systemctl output");
            }
            pid = Long.parseLong(line.substring("MainPID=".length()));
            return pid;
        }
    }

    public void deploy() throws IOException {
        new ProcessBuilder("jvmctl", "deploy", name)
                .inheritIO()
                .redirectOutput(logPath("deploy.log").toFile())
                .redirectErrorStream(true)
                .start();
    }
}
