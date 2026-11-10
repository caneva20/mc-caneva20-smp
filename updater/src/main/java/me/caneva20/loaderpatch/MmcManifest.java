package me.caneva20.loaderpatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reader/writer for Prism & MultiMC's mmc-pack.json (format version 1).
 *
 * Patch semantics follow upstream packwiz-installer's LauncherUtils: the
 * modloader component is replaced by a bare {"uid","version"} object so the
 * launcher re-resolves the loader metadata fresh (no stale cached hints).
 * The component keeps its array position.
 */
public final class MmcManifest {
    /** Component order used by MultiMC/Prism (mirrors packwiz-installer). */
    private static final Map<String, Integer> ORDER = new LinkedHashMap<>();
    static {
        ORDER.put("net.minecraft", -2);
        ORDER.put("org.lwjgl", -1);
        ORDER.put("org.lwjgl3", -1);
        ORDER.put("net.minecraftforge", 5);
        ORDER.put("net.neoforged", 5);
        ORDER.put("net.fabricmc.fabric-loader", 10);
        ORDER.put("org.quiltmc.quilt-loader", 10);
        ORDER.put("com.mumfrey.liteloader", 10);
        ORDER.put("net.fabricmc.intermediary", 11);
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final JsonObject root;

    private MmcManifest(JsonObject root) {
        this.root = root;
    }

    public static MmcManifest load(Path file) throws IOException {
        JsonObject parsed = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        JsonElement fmt = parsed.get("formatVersion");
        if (fmt == null || !fmt.isJsonPrimitive() || fmt.getAsInt() != 1) {
            throw new IOException("unsupported mmc-pack.json formatVersion "
                    + (fmt == null ? "(missing)" : fmt.getAsString()));
        }
        return new MmcManifest(parsed);
    }

    public static String componentVersionIfPossible(Path file) {
        try {
            return load(file).componentVersion("net.neoforged");
        } catch (Exception e) {
            return null;
        }
    }

    public JsonArray components() {
        JsonArray comps = root.getAsJsonArray("components");
        if (comps == null) {
            throw new IllegalArgumentException("invalid mmc-pack.json: no components array");
        }
        return comps;
    }

    public String componentVersion(String uid) {
        JsonObject c = find(uid);
        return c == null ? null : optString(c, "version");
    }

    public String minecraftVersion() {
        return componentVersion("net.minecraft");
    }

    /**
     * @return true if the component existed (updated in place), false if it
     *         was appended at the end.
     */
    public boolean patchComponent(String uid, String version) {
        JsonArray comps = components();
        JsonObject bare = new JsonObject();
        bare.addProperty("uid", uid);
        bare.addProperty("version", version);
        for (int i = 0; i < comps.size(); i++) {
            JsonElement el = comps.get(i);
            if (el.isJsonObject() && uid.equals(optString(el.getAsJsonObject(), "uid"))) {
                comps.set(i, bare);
                return true;
            }
        }
        comps.add(bare);
        return false;
    }

    public void saveTo(Path target) throws IOException {
        Path tmp = target.resolveSibling(target.getFileName() + ".c20tmp");
        Files.writeString(tmp, GSON.toJson(root));
        try {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private JsonObject find(String uid) {
        for (JsonElement el : components()) {
            if (el.isJsonObject() && uid.equals(optString(el.getAsJsonObject(), "uid"))) {
                return el.getAsJsonObject();
            }
        }
        return null;
    }

    private static String optString(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonPrimitive() ? e.getAsString() : null;
    }
}
