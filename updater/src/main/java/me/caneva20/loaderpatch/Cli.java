package me.caneva20.loaderpatch;

import java.nio.file.Paths;

/** Args of the detached helper. */
final class Cli {
    final String instance;      // Prism instance root (contains mmc-pack.json)
    final String packUrl;       // null = resolve from instance.cfg's prelaunch command
    final String data;          // scratch state dir (.c20-loaderpatch)
    final boolean popup;
    final boolean allowRelaunch;
    final long quietWindowMs;
    final long maxWaitMs;

    private Cli(String instance, String packUrl, String data, boolean popup,
                boolean allowRelaunch, long quietWindowMs, long maxWaitMs) {
        this.instance = instance;
        this.packUrl = packUrl;
        this.data = data;
        this.popup = popup;
        this.allowRelaunch = allowRelaunch;
        this.quietWindowMs = quietWindowMs;
        this.maxWaitMs = maxWaitMs;
    }

    static Cli parse(String[] args) {
        String instance = null, packUrl = null, data = null;
        boolean popup = true, allowRelaunch = true;
        long quiet = 1000, maxWait = 20_000;
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
                case "--instance" -> instance = args[++i];
                case "--pack-url" -> packUrl = args[++i];
                case "--data" -> data = args[++i];
                case "--no-popup" -> popup = false;
                case "--no-relaunch" -> allowRelaunch = false;
                case "--quiet-window-ms" -> quiet = Long.parseLong(args[++i]);
                case "--max-wait-ms" -> maxWait = Long.parseLong(args[++i]);
                default -> { /* ignore unknowns for forwards compat */ }
            }
        }
        if (instance == null) {
            // fallback: parent of the working dir (game runs with cwd=.minecraft)
            instance = Paths.get("").toAbsolutePath().getParent().toString();
        }
        if (data == null) {
            data = Paths.get(instance).resolve(".c20-loaderpatch").toString();
        }
        boolean headless = java.awt.GraphicsEnvironment.isHeadless();
        if (headless) {
            popup = false;
            allowRelaunch = false;
        }
        boolean p = popup, rel = allowRelaunch;
        return new Cli(instance, packUrl, data, p, rel, quiet, maxWait);
    }
}
