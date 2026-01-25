# Plan: Beautiful Day Counter

Goal: Track in-game days and present a subtle HUD counter with an optional hotkey to toggle
visibility. On each new day, display a slide-in/out toast similar to the OST song toast. Future
add-ons: custom day label, moon phase, weather icon.

Phases
- P1 Core (server/day logic):
  - Read overworld `timeOfDay`; compute `day = floor(t/24000)+1`.
  - Detect day change on server tick; broadcast to clients via a custom packet.
- P2 Client HUD + toggle:
  - Client-only keybinding to show/hide the overlay.
  - Lightweight HUD renderer that draws the current day in the corner.
- P3 Toast notification:
  - Client packet handler triggers a toast "Day N" with OST-like styling.
  - Add basic config for label text (e.g., "Day", "Sol").
- P4 Polish + config:
  - Persist per-world or global config for visibility and label.
  - Optional: append moon phase and predicted weather as icons.

Testing
- Unit-test day computation from tick times.
- GameTest or manual: verify toast appears once per new day.

Packaging
- Build: `./gradlew :mods:beautiful_day_counter:build` and `:remapJar`.
- Run dev: `./gradlew :mods:beautiful_day_counter:runClient|runServer`.

