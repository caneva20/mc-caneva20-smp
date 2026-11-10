# c20-loaderpatch

Backstory: since Prism 9.3 the pre-launch packwiz flow can no longer apply
modloader version bumps (Prism reads `mmc-pack.json` before the pre-launch
command and overwrites it when the game exits: PrismLauncher #3944 / #4126).

This tiny companion mod restores fully automatic NeoForge bumps for players,
with zero setup on their side:

1. The mod is distributed through the normal packwiz pipeline (it updates
   itself like any other mod in the pack: no re-import, no manual step).
2. When the game shuts down, it compares the NeoForge version recorded in
   `mmc-pack.json` with the one in the hosted `pack.toml`.
3. If they differ, it starts a detached helper that waits for Prism to finish
   its exit bookkeeping, then swaps the NeoForge component in `mmc-pack.json`.
4. A small popup confirms the update is ready; the next Play click runs the
   new loader. No auto-relaunch, no extra dialogs on normal launches.

On launch `N` the player still runs the previous patch build for a single
session (loader patches are binary compatible within one Minecraft version,
and this matches what players were used to before the Prism regression).
Minecraft version jumps intentionally do nothing here: pre-launch packwiz
keeps mod updates working, and those upgrades will become automatic again on
Prism >= 12 (pre-load command) — with future code, the mod could gain support.

## Build

Uses the Java 21 runtime Prism already manages (downloaded by Prism under
`~/.local/share/PrismLauncher/java/java-runtime-delta`), so no extra JDK is
needed. FML + gson libraries also come from Prism's library cache of the
game: no network at build time.

Nothing about the pack is baked into the mod: the helper reads the pack URL
from the instance's own `PreLaunchCommand` line in `instance.cfg` (the same
command that runs packwiz-installer), so it follows whatever the player's
instance was imported with. `--pack-url` remains as an explicit override for
tests.

```sh
./build.sh          # -> build/c20_loaderpatch.jar, + run tests
```

`PRISM_LAUNCHER_DIR` can override the Prism data dir if needed.

## Test the helper standalone (without the game)

```sh
java -cp build/classes:<gson.jar> me.caneva20.loaderpatch.Main \
  --instance /path/to/SomeInstance \
  [--pack-url http://...] [--no-popup] [--quiet-window-ms 1000]
```

Without `--pack-url` the helper uses the URL from the instance's prelaunch
command.

## Layout

- `src/main/java/.../ModEntry.java` — compiled against FML: only registers a
  JVM shutdown hook. No NeoForge APIs beyond the `@Mod` annotation.
- `src/main/java/.../Updater.java` — decides whether the helper even needs to
  start (fast no-op on the vast majority of launches).
- `src/main/java/.../Main.java` — detached helper: checks, waits, patches,
  notifies; optional relaunch via the running Prism.
- `src/test/java` — minimal single-class test runner (no test framework dep).

## Ship it (maintainer side)

When packaging the mockpack zip, add the built jar to `minecraft/mods/`, run
`packwiz add` on it (or hand-write the `.pw.toml`: this is a self-hosted file
without an update source, so packwiz syncs it by hash; set `side = "client"`).
Fresh imports then get the mod too. Existing players receive it on their next
launch: no action, no message on their side.
