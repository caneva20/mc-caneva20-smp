package me.caneva20.loaderpatch;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads settings out of instance.cfg (instance name, packwiz pack URL). */
final class InstanceUtils {

    private InstanceUtils() {
    }

    static String instanceName(Path instanceDir) {
        String text = readCfg(instanceDir);
        if (text == null) {
            return null;
        }
        Matcher m = Pattern.compile("(?m)^name=(.*)$").matcher(text);
        if (m.find()) {
            return unquote(m.group(1).trim());
        }
        return null;
    }

    /**
     * The pack URL the pre-launch packwiz command uses (single source of
     * truth: whatever the instance was imported with). Returns null when the
     * instance.cfg has no such command.
     */
    static String packUrl(Path instanceDir) {
        String text = readCfg(instanceDir);
        if (text == null) {
            return null;
        }
        Matcher line = Pattern.compile("(?m)^PreLaunchCommand=.*$").matcher(text);
        if (!line.find()) {
            return null;
        }
        String cmd = line.group();
        // preferred: the pack file itself ends in pack.toml
        Matcher u = Pattern.compile("https?://\\S*pack\\.toml\\S*", Pattern.CASE_INSENSITIVE)
                .matcher(cmd);
        String url = u.find() ? u.group() : null;
        if (url == null) {
            // fallback: the pack URL is the last argument of the packwiz
            // prelaunch command
            Matcher any = Pattern.compile("https?://\\S+").matcher(cmd);
            while (any.find()) {
                url = any.group();
            }
        }
        if (url == null) {
            return null;
        }
        return url.replaceAll("[\"\\\\]+$", "");
    }

    private static String readCfg(Path instanceDir) {
        try {
            return Files.readString(instanceDir.resolve("instance.cfg"),
                    StandardCharsets.UTF_8);
        } catch (Throwable t) {
            return null;
        }
    }

    private static String unquote(String v) {
        if (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) {
            return v.substring(1, v.length() - 1);
        }
        return v;
    }
}
