package me.caneva20.loaderpatch;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Detached helper process started from the game's shutdown hook (not from the
 * launcher). Sequence:
 *
 *  1. park a pid lock (only one helper at a time)
 *  2. wait for Prism's post-exit write of mmc-pack.json to settle
 *  3. if the NeoForge component differs from the pack.toml target: patch it
 *  4. popup (only now) with an optional one-click relaunch
 *
 * Every step is idempotent; failing any of them leaves the machine in today's
 * state (the player presses Play again later), never something worse.
 */
public final class Main {
    public static void main(String[] rawArgs) {
        Cli cfg = Cli.parse(rawArgs);
        Path instance = Paths.get(cfg.instance);
        Path data = Paths.get(cfg.data);
        Path mmc = instance.resolve("mmc-pack.json");
        Log log = new Log(data);

        Path lock = data.resolve("lock.pid");
        try {
            Files.createDirectories(data);
            if (!createLock(lock)) {
                log.info("another helper instance is running; exiting");
                return;
            }
        } catch (Exception e) {
            log.error("could not create lock: " + e);
            return;
        }

        try {
            run(cfg, instance, data, mmc, log);
        } catch (Throwable t) {
            log.error("fatal: " + t);
        } finally {
            try {
                Files.deleteIfExists(lock);
            } catch (Exception ignored) {
            }
        }
    }

    private static void run(Cli cfg, Path instance, Path data, Path mmc, Log log) throws Exception {
        String name = InstanceUtils.instanceName(instance);
        if (name == null) {
            name = instance.getFileName().toString();
        }
        log.info("helper pid " + ProcessHandle.current().pid() + " for instance '" + name + "'");

        if (!Files.isRegularFile(mmc)) {
            log.info("no mmc-pack.json - not a Prism/MultiMC instance; nothing to do");
            return;
        }

        PackToml pack = null;
        String packUrl = cfg.packUrl;
        if (packUrl == null) {
            packUrl = InstanceUtils.packUrl(instance);
            if (packUrl != null) {
                log.info("pack url from instance.cfg prelaunch command");
            } else {
                log.info("no --pack-url given and none found in instance.cfg; "
                        + "cannot determine target version");
            }
        }
        if (packUrl != null) {
            pack = PackToml.fetch(packUrl, data.resolve("pack-cache.toml"), log);
            if (pack == null) {
                pack = PackToml.fromCache(data.resolve("pack-cache.toml"), log);
                if (pack != null) {
                    log.info("using cached pack.toml (fresh fetch failed)");
                }
            }
        }
        if (pack == null) {
            log.error("no target version known (offline?); leaving everything as is");
            return;
        }
        if (pack.neoforge == null || pack.minecraft == null) {
            log.info("pack.toml has no neoforge/minecraft in [versions]; nothing to do");
            return;
        }
        String target = pack.neoforge;
        writePending(data, target);

        // Wait for Prism to finish its exit-time save of instance metadata.
        QuietWaiter.waitAndSettle(mmc, cfg.quietWindowMs, cfg.maxWaitMs, log);

        MmcManifest manifest = MmcManifest.load(mmc);
        String current = manifest.componentVersion("net.neoforged");

        if (current != null && current.equals(target)) {
            log.info("already up to date (" + target + "); no writes");
            return;
        }

        // Only NeoForge *patch* updates are automatic; Minecraft version jumps
        // must stay manual until Prism's pre-load command ships (>= 12).
        String instMc = manifest.minecraftVersion();
        if (instMc != null && !instMc.equals(pack.minecraft)) {
            log.info("Minecraft version differs (pack " + pack.minecraft + ", instance "
                    + instMc + "); not switching loader across versions");
            return;
        }

        boolean existed = manifest.patchComponent("net.neoforged", target);
        manifest.saveTo(mmc);
        log.info((existed ? "patched" : "added") + " net.neoforged -> " + target
                + (current != null ? " (was " + current + ")" : ""));

        if (cfg.popup) {
            if (Notifier.notify(name, current, target, cfg.allowRelaunch, log)) {
                Relauncher.tryRelaunch(name, log);
            }
        }
    }

    private static void writePending(Path data, String target) {
        try {
            Files.writeString(data.resolve("target.txt"), target, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }

    private static boolean createLock(Path lock) throws Exception {
        try {
            if (Files.exists(lock)) {
                String cur = Files.readString(lock).trim();
                try {
                    long pid = Long.parseLong(cur);
                    boolean alive = ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
                    if (alive) {
                        return false; // genuinely still running
                    }
                } catch (NumberFormatException ignored) {
                    // corrupt/stale lock; take it over
                }
            }
            Files.writeString(lock, String.valueOf(ProcessHandle.current().pid()),
                    StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            if (Files.exists(lock) && Files.size(lock) > 0) {
                // could not read/parse but file exists created this instant?
                return false;
            }
            throw e;
        }
    }
}
