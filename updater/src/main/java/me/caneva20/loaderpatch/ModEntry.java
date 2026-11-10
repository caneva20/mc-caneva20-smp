package me.caneva20.loaderpatch;

import net.neoforged.fml.common.Mod;

/**
 * Closing traceback for the launcher update: registers a shutdown hook at mod
 * construction time so the detached helper can be spawned once the game
 * finishes. Everything practical lives in {@link Updater}.
 *
 * <p>Runs on both sides harmless: on a furnace/server there is no
 * mmc-pack.json so {@link Updater} returns immediately.</p>
 */
@Mod("c20_loaderpatch")
public class ModEntry {
    public ModEntry() {
        Runtime.getRuntime().addShutdownHook(new Thread(ModEntry::hook, "C20-LoaderPatch-Shutdown"));
    }

    private static void hook() {
        try {
            Updater.onShutdown();
        } catch (Throwable t) {
            // never break game shutdown
        }
    }
}
