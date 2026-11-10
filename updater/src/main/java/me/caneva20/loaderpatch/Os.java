package me.caneva20.loaderpatch;

/** Tiny OS flags; nothing clever. */
final class Os {
    static final boolean IS_WINDOWS =
            System.getProperty("os.name", "").toLowerCase().contains("windows");
    static final boolean IS_MAC =
            System.getProperty("os.name", "").toLowerCase().contains("mac");

    private Os() {
    }
}
