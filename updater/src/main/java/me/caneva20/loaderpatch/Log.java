package me.caneva20.loaderpatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

final class Log {
    private final Path file;

    Log(Path dataDir) {
        Path f = null;
        try {
            Files.createDirectories(dataDir);
            f = dataDir.resolve("log.txt");
            if (Files.exists(f) && Files.size(f) > 512 * 1024) {
                Files.move(f, f.resolveSibling("log.1.txt"),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
        }
        this.file = f;
    }

    void info(String msg) {
        write("INFO  " + msg);
    }

    void error(String msg) {
        write("ERROR " + msg);
    }

    private synchronized void write(String msg) {
        if (file == null) {
            return;
        }
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(LocalDateTime.now() + " " + msg);
            w.newLine();
        } catch (IOException ignored) {
        }
    }
}
