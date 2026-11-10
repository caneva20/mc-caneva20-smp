#!/usr/bin/env bash
# Builds the c20-loaderpatch mod jar using the Java 21 runtime by Prism,
# and compiles against the FML + gson jars from the same launcher's library
# cache, so nothing else is needed on the machine.
set -euo pipefail

cd "$(dirname "$0")"

PRISM_LAUNCHER_DIR="${PRISM_LAUNCHER_DIR:-$HOME/.local/share/PrismLauncher}"
JDK="${JDK:-$PRISM_LAUNCHER_DIR/java/java-runtime-delta}"

JAVAC="$JDK/bin/javac"
JAVA="$JDK/bin/java"
JARTOOL="$JDK/bin/jar"

for tool in "$JAVAC" "$JAVA" "$JARTOOL"; do
  if [ ! -x "$tool" ]; then
    echo "missing $tool - set JDK=... (a Java 21 JDK)' and retry" >&2
    exit 1
  fi
done

FML_LOADER="$(find "$PRISM_LAUNCHER_DIR/libraries/net/neoforged/fancymodloader/loader" -name 'loader-4.0.*.jar' 2>/dev/null | sort -V | tail -n 1)"
# (loader-4.0.x = the FML family NeoForge 21.1 uses; newer jars may exist for other MC lines)
GSON="$(find "$PRISM_LAUNCHER_DIR/libraries/com/google/code/gson/gson" -name 'gson-*.jar' 2>/dev/null | sort -V | tail -n 1)"
for lib in "$FML_LOADER" "$GSON"; do
  if [ -z "$lib" ] || [ ! -f "$lib" ]; then
    echo "missing library from Prism's cache: $lib" >&2
    echo "launch the game once so Prism downloads it, or pass libs manually." >&2
    exit 1
  fi
done
echo "FML: $FML_LOADER"
echo "GSON: $GSON"

rm -rf build
mkdir -p build/classes build/test-classes

echo "compiling main sources..."
mapfile -t MAIN_SRC < <(find src/main/java -name '*.java')
"$JAVAC" --release 21 -encoding UTF-8 -cp "$FML_LOADER:$GSON" -d build/classes "${MAIN_SRC[@]}"

# pack the mod: classes + neoforge.mods.toml
mkdir -p build/classes/META-INF
cp src/main/resources/META-INF/neoforge.mods.toml build/classes/META-INF/
"$JARTOOL" --create --file build/c20_loaderpatch.jar -C build/classes .
echo "built build/c20_loaderpatch.jar"

echo "compiling tests..."
mapfile -t TEST_SRC < <(find src/test/java -name '*.java')
"$JAVAC" --release 21 -encoding UTF-8 -cp "build/classes:$GSON" -d build/test-classes "${TEST_SRC[@]}"

echo "running tests..."
"$JAVA" -cp "build/test-classes:build/classes:$GSON" me.caneva20.loaderpatch.TestAll
echo "done."
