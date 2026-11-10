package me.caneva20.loaderpatch;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs on the game's shutdown hook. Extremely cheap: on the usual "nothing to
 * do" launch this is a couple of file reads and no process is spawned. The
 * helper itself discovers the pack URL from the instance's own prelaunch
 * command, so nothing about the pack is baked in here.
 */
public final class Updater {

    private Updater() {
    }

    public static void onShutdown() {
        Path mcDir = Paths.get("").toAbsolutePath();
        Path instance = mcDir.getParent();
        if (instance == null) {
            return;
        }
        Path mmc = instance.resolve("mmc-pack.json");
        if (!Files.isRegularFile(mmc)) {
            return; // not a Prism/MultiMC instance (server, standalone client...)
        }
        Path data = mcDir.resolve(".c20-loaderpatch");
        if (Files.isRegularFile(data.resolve("disabled"))) {
            return; // support kill-switch
        }

        // Fast skip: the helper rewrites target.txt whenever it syncs, so when
        // the file already matches what is in mmc-pack.json there is nothing
        // pending and we avoid starting a process at all.
        String current = MmcManifest.componentVersionIfPossible(mmc);
        String pending = readPending(data);
        if (current != null && current.equals(pending)) {
            return;
        }

        String jarPath = selfJarPath();
        if (jarPath == null) {
            return; // not running from a jar (development)
        }
        String javaBin = Paths.get(System.getProperty("java.home"), "bin",
                Os.IS_WINDOWS ? "java.exe" : "java").toString();

        List<String> cmd = new ArrayList<>();
        if (Os.IS_WINDOWS) {
            cmd.add("cmd");
            cmd.add("/c");
            cmd.add("start");
            cmd.add("");
            cmd.add("/min");
        }
        cmd.add(javaBin);
        cmd.add("-Xmx128m");
        cmd.add("-cp");
        cmd.add(jarPath);
        cmd.add(Main.class.getName());
        cmd.add("--instance");
        cmd.add(instance.toString());
        cmd.add("--data");
        cmd.add(data.toString());

        try {
            Files.createDirectories(data);
            Path out = data.resolve("log.txt");
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(mcDir.toFile());
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(out.toFile()));
            pb.start(); // child outlives this dying process
        } catch (Throwable ignored) {
            // best effort only
        }
    }

    private static String readPending(Path data) {
        Path f = data.resolve("target.txt");
        try {
            return Files.readString(f, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static String selfJarPath() {
        try {
            java.net.URI uri = Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path p = Paths.get(uri);
            if (!p.toString().toLowerCase().endsWith(".jar")) {
                return null;
            }
            return p.toString();
        } catch (Throwable t) {
            return null;
        }
    }
}
