# Pinned mods & known conflicts

Why each pinned mod is pinned. Do not unpick a pin (or update the mod) without
re-checking the reason below. Last reviewed: 2026-11-11 (v2.3.2).

## Pinned in v2.3.1 — TMRV JEI stub limit

The pack uses EMI + TMRV for the recipe viewer. TMRV fakes a `jei` mod at
version 19.27.0.343 and there is no real JEI in the pack. Upstream has no fix
(Nolij/TooManyRecipeViewers#84 — author inactive, JEI API changed underneath,
thread locked Oct 2026).

- **sophisticated-core** (1.4.44.1923) — 1.5.x requires `jei >= 19.32`.
- **sophisticated-backpacks** (3.25.51.1801) — same sweep, kept in step with core.
- **sophisticated-backpacks-create-integration** (0.1.5.29) — 0.2.x requires `jei >= 19.32`;
  0.1.5.29 only wants `jei >= 19.12` (optional).
- **strut-your-stuff** (1.3.1), **azimuth-api** (1.4.10) — required by bits_n_bobs
  (`struts >= 1.0.0`, `azimuth >= 1.4.6`); pinned so sweeps can't yank them out
  from under it.

Escape hatches if the sophisticated trio must be upgraded before TMRV fixes the stub:
- Test TMRV `0.9.1-dev.4` (GitHub-only, Aug 2026) — its stub claims `jei 19.44`,
  but unverified in practice (thread reports recipes not loading for other mods).
- NeoForge 1.21.1+ dependency override in `config/fml.toml`, e.g.
  `sophisticatedcore = ["-jei"]` under `[dependencyOverrides]` — risky, mods may
  genuinely use the newer JEI API and crash.

## Pinned in v2.3.2 — Copper Age duplicate registration

- **vanillabackport** (1.1.7.4) — VanillaBackport 1.2.0+ added the Copper Age
  drop (copper chest, copper golem, copper armor, `copper_fire_flame`, ...).
  Backport Copper Age registers the same vanilla-namespace keys, so both crash
  with duplicate-key errors during registry init, cascading into 11 unbound
  errors (mynethersdelight, aeronautics, extra_gauges, minecolonies) and a
  fatal broken-mod-state crash.

Path to re-upgrade: remove `backport-copper-age` first, then unpin +
update vanillabackport. Same vanilla IDs, so a swap should be mostly
seamless for existing worlds — verify item/recipe content overlap first.

## Other standing pins (older decisions)

- **easyauth**, **easywhitelist** — server auth plugins, deliberate.
- Skip-list for sweeps (not pinned, just excluded): connector, c2me (alpha),
  kubejs/rhino (script deps), worldgen mods (terralith, tectonic, lithostitched,
  biolith, regions-unexplored).
