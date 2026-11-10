#!/usr/bin/env bash
# Rebuilds the public 'release' branch as ONE squashed commit containing ONLY
# the files a packwiz player ever needs to download:
#
#   - pack.toml, index.toml
#   - every file listed in index.toml (the .pw.toml metafiles the installer
#     fetches)
#   - any file a metafile's download URL points back at this pack
#     (e.g. the c20_loaderpatch jar)
#   - the importable mockpack zip
#
# No sources, no scripts, no README. Serving the files stays wherever it
# already is; the branch is only the public mirror.
#
#   ./release.sh           rebuild the local 'release' branch
#   ./release.sh --push    ... and force-push it to origin
set -euo pipefail
cd "$(dirname "$0")"

BRANCH=release
PUSH=0
case "${1:-}" in
  "") ;;
  --push) PUSH=1 ;;
  -h|--help) awk 'NR>=1&&NR<=16' "$0" | tail -12; exit 0 ;;
  *) echo "usage: $0 [--push]" >&2; exit 2 ;;
esac

if [ "$(git branch --show-current)" = "$BRANCH" ]; then
  echo "HEAD is on $BRANCH; switch to master before rebuilding it" >&2
  exit 1
fi

T="$(mktemp -d)"
trap 'rm -rf "$T"' EXIT

TO_ADD=()   # repo-relative paths, in tree order
add() {
  local f="$1"
  if [ ! -e "$f" ]; then
    echo "WARNING: referenced file missing: $f" >&2
    return
  fi
  TO_ADD+=("$f")
}

add pack.toml
add index.toml

# everything the index tells the installer to fetch from this pack
mapfile -t INDEX_FILES < <(
  awk -F'"' '/^[[:space:]]*file[[:space:]]*=/ {print $2}' index.toml | grep . | sort -u
)
for f in "${INDEX_FILES[@]}"; do
  add "$f"
done

# local downloads referenced by the metafiles themselves (percent-decoded so
# "caneva20%20smp%20s2.zip"-style names map back to real filenames)
mapfile -t SELF < <(
  for f in "${INDEX_FILES[@]}"; do
    [ "${f##*.}" != pw.toml ] && continue
    grep -hoE 'url[[:space:]]*=[[:space:]]*"[^"]*files\.caneva20\.me/modpacks/mc/smp-s2/[^"]*"' "$f" | \
    grep -oE 'modpacks/mc/smp-s2/[^"]*'
  done |
  sed -e 's|^modpacks/mc/smp-s2/||' -e 's/%20/ /g' |
  sort -u
)
for f in "${SELF[@]}"; do
  add "$f"
done

# importable zip at the repo root (ignored by packwiz by design)
mapfile -d '' ZIPS < <(find . -maxdepth 1 -type f -name '*.zip' -print0)
for f in "${ZIPS[@]}"; do
  add "${f#./}"
done

# commit-tree plumbing: touched objects go to the repo, the real index never
# sees any of this
export GIT_INDEX_FILE="$T/git-index"
rm -f "$GIT_INDEX_FILE"
for f in "${TO_ADD[@]}"; do
  git add -- "$f"
done
TREE=$(git write-tree)
unset GIT_INDEX_FILE

VERSION=$(awk -F'"' '/^version[[:space:]]*=/ {print $2; exit}' pack.toml)
MASTER=$(git rev-parse --short HEAD)
COMMIT=$(git commit-tree "$TREE" -m "Public pack v$VERSION (snapshot of master $MASTER)")
git update-ref "refs/heads/$BRANCH" "$COMMIT"

COUNT=$(git ls-tree -r --name-only "$COMMIT" | wc -l)
echo "$BRANCH -> $COMMIT ($COUNT files, pack v$VERSION)"
git ls-tree --name-only "$COMMIT" | sed 's/^/  /'

if [ "$PUSH" = 1 ]; then
  git push --force origin "$BRANCH"
fi
