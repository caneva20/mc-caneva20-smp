package me.caneva20.loaderpatch;

import com.google.gson.JsonArray;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dependency-free test runner; run with: java -cp ... TestAll
 * Exits non-zero (with a stack trace) on the first failure.
 */
public final class TestAll {
    private static final AtomicLong PASS = new AtomicLong();
    private static final AtomicLong FAIL = new AtomicLong();

    // same shape as the mmc-pack.json shipped in the mockpack zip
    private static final String MMC = """
        {
          "components": [
            {"cachedName": "Minecraft", "cachedRequires": [{"suggests": "3.3.3", "uid": "org.lwjgl3"}],
             "cachedVersion": "1.21.1", "important": true, "uid": "net.minecraft", "version": "1.21.1"},
            {"cachedName": "LWJGL 3", "cachedVersion": "3.3.3", "cachedVolatile": true,
             "dependencyOnly": true, "uid": "org.lwjgl3", "version": "3.3.3"},
            {"cachedName": "NeoForge", "cachedRequires": [{"equals": "1.21.1", "uid": "net.minecraft"}],
             "cachedVersion": "21.1.228", "important": true, "uid": "net.neoforged", "version": "21.1.228"}
          ],
          "formatVersion": 1
        }""";
    private static final String PACK = """
        name = "caneva20 SMP S2"
        author = "caneva20"
        version = "2.2.0"
        pack-format = "packwiz:1.1.0"

        # a comment w/ brackets [index] must not break the parse
        [index]
        file = "index.toml"
        hash-format = "sha256"

        [versions]
        minecraft = "1.21.1"
        neoforge = "21.1.228"
        """;

    public static void main(String[] args) {
        run("packToml parses versions section", TestAll::testPackToml);
        run("mmc-pack parses current neoforge version", TestAll::testMmcRead);
        run("patch replaces component and clears stale hints", TestAll::testMmcPatch);
        run("missing loader component is appended", TestAll::testMmcAppend);
        run("patched file reloads as format 1", TestAll::testMmcRoundtrip);
        run("instance name parsing", TestAll::testInstanceName);
        run("pack url resolved from instance.cfg", TestAll::testPackUrlResolution);
        run("quiet waiter observes change then settles", TestAll::testQuiet);
        run("quiet waiter: no change stays bounded", TestAll::testQuietNoChange);
        run("cli defaults", TestAll::testCli);

        System.out.println();
        System.out.println("passed=" + PASS.get() + " failed=" + FAIL.get());
        if (FAIL.get() > 0) {
            System.exit(1);
        }
    }

    interface TestCase {
        void run() throws Exception;
    }

    private static void run(String name, TestCase tc) {
        System.out.print("[ " + name + " ] ... ");
        try {
            tc.run();
            PASS.incrementAndGet();
            System.out.println("ok");
        } catch (Throwable t) {
            FAIL.incrementAndGet();
            System.out.println("FAILED: " + t);
            t.printStackTrace(System.out);
        }
    }

    private static void testPackToml() {
        PackToml p = PackToml.parse(PACK);
        check(p.minecraft.equals("1.21.1"), p.minecraft);
        check(p.neoforge.equals("21.1.228"), p.neoforge);
    }

    private static void testMmcRead() throws Exception {
        Path f = Files.createTempFile("mmc", ".json");
        Files.writeString(f, MMC);
        MmcManifest m = MmcManifest.load(f);
        check("21.1.228".equals(m.componentVersion("net.neoforged")), "read loader");
        check("1.21.1".equals(m.minecraftVersion()), "read mc");
        Files.deleteIfExists(f);
    }

    private static void testMmcPatch() throws Exception {
        Path f = Files.createTempFile("mmc", ".json");
        Files.writeString(f, MMC);
        MmcManifest m = MmcManifest.load(f);
        boolean existed = m.patchComponent("net.neoforged", "21.1.230");
        check(existed, "existed");
        m.saveTo(f);
        String out = Files.readString(f);
        check(out.contains("21.1.230"), out);
        check(!out.contains("21.1.228"), "old version still present in " + out);
        // only the patched component must have been replaced by a bare object
        m = MmcManifest.load(f);
        com.google.gson.JsonObject neo = null;
        for (var el : m.components()) {
            if (el.isJsonObject() && "net.neoforged".equals(el.getAsJsonObject().get("uid").getAsString())) {
                neo = el.getAsJsonObject();
            }
        }
        check(neo != null, "loader component present");
        check(neo.keySet().size() == 2 && neo.has("uid") && neo.has("version"),
                "loader component stripped to {uid,version}: " + neo);
        check("1.21.1".equals(m.minecraftVersion()), "mc intact");
        Files.deleteIfExists(f);
    }

    private static void testMmcAppend() throws Exception {
        Path f = Files.createTempFile("mmc2", ".json");
        Files.writeString(f, MMC);
        MmcManifest m = MmcManifest.load(f);
        com.google.gson.JsonArray comps = m.components();
        java.util.List<com.google.gson.JsonElement> drop = new java.util.ArrayList<>();
        for (var el : comps) {
            if (el.isJsonObject()
                    && "net.neoforged".equals(el.getAsJsonObject().get("uid").getAsString())) {
                drop.add(el);
            }
        }
        drop.forEach(comps::remove);
        check(comps.size() == 2, "removed loader component");
        m.saveTo(f);
        m = MmcManifest.load(f);
        check(m.componentVersion("net.neoforged") == null, "absent at start");
        check(!m.patchComponent("net.neoforged", "21.1.230"), "was appended");
        check("21.1.230".equals(m.componentVersion("net.neoforged")), "appended value");
        Files.deleteIfExists(f);
    }

    private static void testMmcRoundtrip() throws Exception {
        Path f = Files.createTempFile("mmc3", ".json");
        Files.writeString(f, MMC);
        MmcManifest m1 = MmcManifest.load(f);
        m1.patchComponent("net.neoforged", "21.1.230");
        m1.saveTo(f);
        MmcManifest m2 = MmcManifest.load(f); // load() itself validates formatVersion == 1
        check("21.1.230".equals(m2.componentVersion("net.neoforged")), "reloaded");
        Files.deleteIfExists(f);
    }

    private static void testInstanceName() throws Exception {
        Path dir = Files.createTempDirectory("inst");
        Files.writeString(dir.resolve("instance.cfg"),
                "name=caneva20 smp s2\nJavaVersion=21.0.7\nother=\"quoted\"\n");
        check("caneva20 smp s2".equals(InstanceUtils.instanceName(dir)), "plain name");
        Path empty = Files.createTempDirectory("instempty");
        check(InstanceUtils.instanceName(empty) == null, "missing cfg tolerated");
    }

    private static void testPackUrlResolution() throws Exception {
        Path dir = Files.createTempDirectory("inst2");
        // exactly what the mockpack's instance.cfg ships
        Files.writeString(dir.resolve("instance.cfg"),
                """
                [General]
                name=caneva20 smp s2
                PreLaunchCommand="$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://files.caneva20.me/modpacks/mc/smp-s2/pack.toml
                """);
        check("https://files.caneva20.me/modpacks/mc/smp-s2/pack.toml"
                .equals(InstanceUtils.packUrl(dir)), "url extracted");
        Path none = Files.createTempDirectory("instnone");
        Files.writeString(none.resolve("instance.cfg"), "name=x\n");
        check(InstanceUtils.packUrl(none) == null, "no prelaunch -> null");
        check(InstanceUtils.packUrl(Files.createTempDirectory("nothing")) == null,
                "missing cfg -> null");
    }

    private static void testQuiet() throws Exception {
        Path dir = Files.createTempDirectory("qq");
        Path f = dir.resolve("file.dat");
        Files.writeString(f, "initial");
        new Thread(() -> {
            try {
                Thread.sleep(600);
                Files.writeString(f, "saved-by-prism");
            } catch (Exception ignored) {
            }
        }).start();
        boolean changed = QuietWaiter.waitAndSettle(f, 500, 10_000, new Log(dir));
        check(changed, "observed change");
        check(Arrays.equals(Files.readAllBytes(f), "saved-by-prism".getBytes()), "final content");
    }

    private static void testQuietNoChange() throws Exception {
        Path dir = Files.createTempDirectory("q2");
        Path f = dir.resolve("static.txt");
        Files.writeString(f, "static");
        long t0 = System.currentTimeMillis();
        boolean changed = QuietWaiter.waitAndSettle(f, 300, 2000, new Log(dir));
        check(!changed, "unchanged");
        check(System.currentTimeMillis() - t0 <= 4500, "bounded runtime");
    }

    private static void testCli() {
        Cli c = Cli.parse(new String[]{"--instance", "/tmp/i", "--no-popup", "--quiet-window-ms", "42"});
        check(c.quietWindowMs == 42 && !c.popup && c.instance.equals("/tmp/i"), "args");
        check(c.packUrl == null, "no default url: resolved from instance.cfg later");
    }

    private static void check(boolean cond, Object detail) {
        if (!cond) {
            throw new IllegalStateException("check failed: " + detail);
        }
    }
}
