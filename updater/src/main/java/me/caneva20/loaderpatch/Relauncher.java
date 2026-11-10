package me.caneva20.loaderpatch;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/** Best-effort discovery of the running Prism and relaunching the instance. */
final class Relauncher {
    private Relauncher() {
    }

    /** @return true when a relaunch command was successfully handed over. */
    static boolean tryRelaunch(String instanceName, Log log) {
        String exe = findLauncher(log);
        if (exe == null) {
            log.info("launcher not found for relaunch; player will click Play again");
            return false;
        }
        try {
            new ProcessBuilder(exe, "--launch", instanceName).start();
            log.info("relaunched via " + exe);
            return true;
        } catch (Throwable t) {
            log.error("relaunch attempt failed: " + t);
            return false;
        }
    }

    private static String findLauncher(Log log) {
        if (Os.IS_WINDOWS) {
            return findWindows(log);
        }
        if (Os.IS_MAC) {
            return findMac();
        }
        return findLinux();
    }

    private static String findLinux() {
        try (Stream<Path> proc = Files.list(Path.of("/proc"))) {
            final List<Path> candidates = new ArrayList<>();
            proc.filter(p -> {
                String n = p.getFileName().toString();
                return n.chars().allMatch(Character::isDigit);
            }).sorted((a, b) -> Long.compare(pid(a), pid(b))).forEach(p -> {
                try {
                    String t = readlink(p);
                    if (t != null && t.toLowerCase().contains("prism")) {
                        candidates.add(p);
                    }
                } catch (Throwable ignored) {
                }
            });
            if (candidates.isEmpty()) {
                return null;
            }
            // lowest pid = the GUI the user has open (game processes nest later)
            return readlink(candidates.get(0));
        } catch (Throwable t) {
            return null;
        }
    }

    private static long pid(Path p) {
        try {
            return Long.parseLong(p.getFileName().toString());
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private static String readlink(Path procEntry) throws Exception {
        byte[] b = Files.readAllBytes(procEntry.resolve("exe"));
        return new String(b, StandardCharsets.UTF_8).trim();
    }

    private static String findMac() {
        try {
            String out = runCommandList("/bin/sh", "-c", "ps -axo comm=");
            for (String line : out.split("\n")) {
                String t = line.trim();
                if (t.isBlank()) {
                    continue;
                }
                if (t.toLowerCase().contains("prismlauncher") || t.toLowerCase().contains("multimc")) {
                    return t;
                }
            }
            return null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static String findWindows(Log log) {
        try {
            String ps = "Get-CimInstance Win32_Process | Where-Object { $_.Name -match 'prism' "
                    + "-and $_.ExecutablePath } | Select-Object -First 1 -ExpandProperty ExecutablePath";
            String out = runCommandList("powershell", "-NoProfile", "-Command", ps);
            String t = out.trim();
            if (t.isEmpty() || t.length() > 400) {
                return null;
            }
            return t;
        } catch (Throwable t) {
            log.error("windows beam detection failed: " + t);
            return null;
        }
    }

    private static String runCommandList(String... cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);
        Process p = pb.start();
        String out;
        try (InputStream in = p.getInputStream()) {
            byte[] b = in.readAllBytes();
            out = new String(b, StandardCharsets.UTF_8);
        }
        if (!p.waitFor(6, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            throw new IllegalStateException("command timed out: " + String.join(" ", cmd));
        }
        return out;
    }
}
