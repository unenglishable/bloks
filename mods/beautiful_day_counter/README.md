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

## How It Works (Technical Overview)
- Day detection
  - Client‑only fallback: Each client reads `timeOfDay`, computes `day = floor(t/24000)+1`, and
    triggers the toast when `day` increments. On join, it initializes to avoid catch‑up toasts.
  - Optional server assist: If present, the server detects day rollovers and sends an S2C packet;
    the client treats this as authoritative.
- Toasts and animation
  - Implemented via the built‑in toast system (`Toast`, `ToastComponent`).
  - Our `DayToast` draws the standard toast background + a clock icon + title text.
  - The toast system handles slide‑in/out animation; we control display duration (3s).
- HUD display
  - Current build: uses the action bar (above the hotbar) and refreshes every ~2 seconds while
    visible. This is minimal and keeps the UI clutter‑free.
  - Planned: replace with a true HUD overlay using Fabric’s HUD render callback for persistent,
    fine‑grained placement and styling.

F1 (Hide GUI) behavior
- Action bar messages: Hidden when F1 is toggled (Hide GUI on) along with other HUD elements.
- Future HUD overlay: We will respect F1 by not rendering when Hide GUI is enabled.

## Configuration
- Mod Menu: Provides a small config screen for label and HUD visibility.
- File: `config/beautiful_day_counter.json` stores the same settings client‑side.

## Files & Links
- Plan: mods/beautiful_day_counter/PLAN.md
- Design prompts: mods/beautiful_day_counter/DESIGN.md
- Client code: mods/beautiful_day_counter/src/client/java/dev/bloks/beautiful_day_counter/client
- Server init: mods/beautiful_day_counter/src/main/java/dev/bloks/beautiful_day_counter

## Roadmap (Next Step)
- Replace action‑bar HUD with a true HUD overlay rendered each frame (Fabric HUD callback), honoring
  F1 and allowing corner positioning and future icons (moon phase, weather).
