package me.caneva20.loaderpatch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * After the game exits, Prism rewrites mmc-pack.json with its in-memory
 * (stale) state. The helper must patch the file only after that write has
 * settled, so it watches the file contents and only acts once the bytes have
 * been stable for a quiet window (or the wait deadline passed).
 */
final class QuietWaiter {
    private QuietWaiter() {
    }

    /**
     * @return true if the file changed at least once since we started
     *         (i.e. Prism's save was observed).
     */
    static boolean waitAndSettle(Path file, long quietWindowMs, long maxWaitMs, Log log) {
        long start = System.currentTimeMillis();
        byte[] last = null;
        boolean changed = false;
        Long quietFrom = null;
        while (System.currentTimeMillis() - start < maxWaitMs) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return changed;
            }
            byte[] now = readOrNull(file);
            if (now == null) {
                // file vanished (transient during a move) => treat as change
                if (last != null) {
                    changed = true;
                    quietFrom = null;
                    last = null;
                }
                continue;
            }
            if (last == null || !Arrays.equals(now, last)) {
                changed = changed || last != null;
                last = now;
                quietFrom = System.currentTimeMillis();
                continue;
            }
            if (quietFrom == null) {
                quietFrom = System.currentTimeMillis();
            }
            long quietFor = System.currentTimeMillis() - quietFrom;
            if (quietFor >= quietWindowMs) {
                if (!changed) {
                    log.info("file was already quiet from the start");
                }
                return changed;
            }
        }
        log.info("settle deadline reached (changed=" + changed + ") - proceeding anyway");
        return changed;
    }

    private static byte[] readOrNull(Path f) {
        try {
            return Files.readAllBytes(f);
        } catch (Exception e) {
            return null;
        }
    }
}
