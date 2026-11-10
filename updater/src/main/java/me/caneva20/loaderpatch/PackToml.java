package me.caneva20.loaderpatch;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Minimal reader for the [versions] section of a packwiz pack.toml; fetching
 * is a plain HTTPS GET (no update machinery needed).
 */
public final class PackToml {
    public final String minecraft;
    public final String neoforge;

    private PackToml(String minecraft, String neoforge) {
        this.minecraft = minecraft;
        this.neoforge = neoforge;
    }

    public static PackToml parse(String text) {
        String section = null;
        String mc = null, neo = null;
        for (String raw : text.split("\n", -1)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }
            if (line.startsWith("[")) {
                section = line.substring(1, line.length() - (line.endsWith("]") ? 1 : 0)).trim();
                continue;
            }
            int eq = line.indexOf('=');
            if (eq <= 0 || !"versions".equals(section)) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            String val = unquote(line.substring(eq + 1).trim());
            if (key.equals("minecraft")) {
                mc = val;
            } else if (key.equals("neoforge")) {
                neo = val;
            }
        }
        return new PackToml(mc, neo);
    }

    private static String unquote(String v) {
        if (v.length() >= 2 && (v.startsWith("\"") && v.endsWith("\"")
                || v.startsWith("'") && v.endsWith("'"))) {
            return v.substring(1, v.length() - 1);
        }
        return v;
    }

    /** Plain HTTPS GET of the pack file. Returns null on any failure (the
     *  caller decides whether the cached copy is still usable). */
    public static PackToml fetch(String packUrl, Path cacheTo, Log log) {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL(packUrl).openConnection();
            c.setConnectTimeout(5_000);
            c.setReadTimeout(15_000);
            c.setRequestProperty("Cache-Control", "no-cache");
            int code = c.getResponseCode();
            if (code != 200) {
                log.error("pack.toml fetch failed: HTTP " + code);
                return null;
            }
            byte[] bytes = c.getInputStream().readAllBytes();
            String text = new String(bytes, StandardCharsets.UTF_8);
            if (cacheTo != null) {
                try {
                    Files.write(cacheTo, bytes);
                } catch (Exception e) {
                    log.error("could not update pack cache: " + e);
                }
            }
            return parse(text);
        } catch (Exception e) {
            log.error("pack.toml fetch failed: " + e);
            return null;
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
    }

    public static PackToml fromCache(Path cache, Log log) {
        try {
            if (!Files.isRegularFile(cache)) {
                return null;
            }
            return parse(new String(Files.readAllBytes(cache), StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }
}
