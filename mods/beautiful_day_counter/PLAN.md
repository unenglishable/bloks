# Plan: Beautiful Day Counter

Goal: Track in-game days and present a subtle HUD counter with an optional hotkey to toggle
visibility. On each new day, display a slide-in/out toast similar to the OST song toast. Future
add-ons: custom day label, moon phase, weather icon.

Phases

- P1 Core (server/day logic):
  - Read overworld `timeOfDay`; compute `day = floor(t/24000)+1`. [Completed]
  - Detect day change on server tick; broadcast to clients via a custom packet. [Completed]
  - Client-only fallback detects day locally. [Completed]
- P2 Client HUD + toggle:
  - Client-only keybinding to show/hide. [Completed]
  - HUD overlay via Fabric HUD render callback; bottom-right by default; corner configurable.
    [Completed]
  - Deprecated: action-bar refresh approach (replaced by proper HUD overlay). [2026-01-26]
- P3 Toast notification:
  - Client shows "Day N" toast with native Toast API and clock icon. [Completed]
  - Basic config for label text (e.g., "Day", "Sol"). [Completed]
- P4 Polish + config:
  - Persist client config (visibility, label, HUD corner, toast enable). [Completed]
  - Localization (lang): convert UI strings to translatables. [Completed]
  - Deprecated (2026-01-27): moon phase icon — removed for simplicity and mappings stability.
  - Deprecated (2026-01-26): positioning offsets — decided corner selection is sufficient.
  - Deprecated (2026-01-26): weather icon/report — not needed now.

Testing

- Unit-test day computation from tick times. [Completed]
- Manual: verify toast appears once per new day; HUD respects F1 and corner config. [Ongoing]

Packaging

- Build: `./gradlew :mods:beautiful_day_counter:build` and `:remapJar`.
- Run dev: `./gradlew :mods:beautiful_day_counter:runClient|runServer`.
- CI tasks (2026-01-27):
  - Add explicit `test` step to GitHub Actions build. [Completed]
  - Update `publish-mod.yml` and `release.yml` to run module tests and share Gradle caching.
    [Completed]

Status

- Networking + client fallback done. HUD overlay implemented. Config + Mod Menu integrated.
- Localization shipped (lang strings + i18n wiring). Unit tests cover day math.
- Deprecated future work: moon phase icon and weather additions unless requirements change.
