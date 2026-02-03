# Developer Guide — Beautiful Day Counter

This file is for contributors. Player-facing docs live in `README.md`.

## Architecture

- Client-only fallback: compute `day = floor(timeOfDay/24000)+1` on client ticks; init on join.
- Server assist (optional): server detects rollovers and sends S2C `DAY_CHANGE`; client treats this
  as authoritative when present.
- UI:
  - HUD overlay via Fabric `HudRenderCallback` (respects F1 hide GUI).
  - Toast via `Toast` API; custom `DayToast` with clock icon (~3s duration).
- Config: JSON `config/beautiful_day_counter.json` and Mod Menu screen; options: label, HUD
  visibility, HUD corner (top_left, top_right, bottom_left, bottom_right).

## Local Dev

- Run client: `./gradlew :mods:beautiful_day_counter:runClient`
- Build jar: `./gradlew :mods:beautiful_day_counter:remapJar`
- Lint/format: `spotlessApply`, `checkstyleMain`, `spotbugsMain`

## Files of Interest

- Client init: `client/BeautifulDayCounterClient.java`
- HUD: HUD callback in client init
- Toast: `client/ui/DayToast.java`
- Networking: `main/BeautifulDayCounter.java` + `main/.../net/DayChangePayload.java` (typed payload
  registered via PayloadTypeRegistry; client uses typed receiver)
- Config: `client/config/*.java`, `fabric.mod.json` (modmenu entrypoint)
- Post-mortem summary: `mixin/client/DeathScreenMixin.java` decorates the score line with the day
  count and difficulty/mode when the death screen renders.
- Audio gag: `assets/.../sounds/hikari_8_bit.ogg` + `sounds.json`. The death screen mixin plays this
  clip once per hardcore death to add a lighthearted moment; replace the file to change the tune.

## Assets & Mod Menu Icon

- ModMenu pulls the icon listed in `fabric.mod.json` (`assets/beautiful_day_counter/icon.png`).
  Replace that file with a square PNG (recommended 64×64 or 128×128) to update the mod list button
  art. The placeholder file in the repo keeps builds happy until an actual design arrives.

## Testing Checklist

- New/existing world; sleep → one toast per new day
- H toggle and F1 hide GUI behavior
- Corner config cycles and persists; overlay positions correctly
- SMP without server mod (fallback) and with server mod (packets)

## Roadmap (Dev)

- Localization (lang files)
- Moon phase + weather icons (toast/HUD)
- Optional overlay offsets/padding
