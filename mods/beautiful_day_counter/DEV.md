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
- Networking: `main/BeautifulDayCounter.java` + `main/.../net/Packets.java`
- Config: `client/config/*.java`, `fabric.mod.json` (modmenu entrypoint)

## Testing Checklist
- New/existing world; sleep → one toast per new day
- H toggle and F1 hide GUI behavior
- Corner config cycles and persists; overlay positions correctly
- SMP without server mod (fallback) and with server mod (packets)

## Roadmap (Dev)
- Localization (lang files)
- Moon phase + weather icons (toast/HUD)
- Optional overlay offsets/padding
