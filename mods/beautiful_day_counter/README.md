# Beautiful Day Counter

A lightweight, vanilla‑feeling day tracker for Minecraft Java (Fabric). Shows a subtle counter and a
slide‑in toast announcing each new day.

## What It Does (Player Guide)
- Day counter: Displays “Day N” unobtrusively. Toggle visibility with H.
- New‑day toast: When a new day begins, a toast slides in saying “Day N”.
- Configurable label: Change "Day" to anything (e.g., "Sol").

How to use
- Toggle: Press H to show/hide the counter.
- Configure: Open Mod Menu → Beautiful Day Counter → Configure. Edit label and HUD visibility.
- Single/multiplayer: Works everywhere. The mod detects day locally; if the server also has the
  mod, it can broadcast authoritative day changes.

Design goal
- We use native UI components so the experience looks and feels like vanilla.

## How It Works (Overview)
- The mod tracks in‑game days and shows a small on‑screen label (HUD overlay) like “Day N”.
- A toast pops in at the start of each new day with the same “Day N” message.
- It’s designed with native UI pieces to match vanilla’s look and feel.

F1 (Hide GUI) behavior
- The HUD overlay respects F1: it does not render when Hide GUI is enabled.

## Configuration
- Mod Menu: Provides a small config screen for label and HUD visibility.
- File: `config/beautiful_day_counter.json` stores the same settings client‑side.

## Fun Facts
- Day/night cycle: A Minecraft day is 24,000 ticks (~20 minutes). We compute days as
  floor(timeOfDay/24000)+1.
- Moon phase: Cycles every 8 days (0 = full moon). We can use this for future icons.
- Weather: The world exposes current rain/thunder state; forecasting needs server data.
- Vanilla style: The day toast uses Minecraft’s built‑in toast system and textures.

## Files & Links
- Plan: mods/beautiful_day_counter/PLAN.md
- Design prompts: mods/beautiful_day_counter/DESIGN.md
- Client code: mods/beautiful_day_counter/src/client/java/dev/bloks/beautiful_day_counter/client
- Server init: mods/beautiful_day_counter/src/main/java/dev/bloks/beautiful_day_counter
 - Developer docs: mods/beautiful_day_counter/DEV.md

## Roadmap (Next Step)
- Replace action‑bar HUD with a true HUD overlay rendered each frame (Fabric HUD callback), honoring
  F1 and allowing corner positioning and future icons (moon phase, weather).
