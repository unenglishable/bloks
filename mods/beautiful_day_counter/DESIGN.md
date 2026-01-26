# Beautiful Day Counter — Design Prompts

Source notes from the product brief (kept for history and reference during development).

- Platform: Minecraft Java, Fabric Loader with Fabric API.
- Server version: 1.21.11.
- Fabric API: 0.141.1 + 1.21.11.
- Mod name: "Beautiful Day Counter".
- Mod ID: `beautiful_day_counter`.

Functionality

- Track the number of days elapsed since world start.
- Display a non-invasive day counter; add a hotkey to hide/show the counter.
- On a new day (after sleep/wake or day-night cycle), show a slide-in/out toast like the OST song
  toast, with text like "Day 9".
- Later config: allow customizing the label (e.g., "Sol 9").
- Visuals: toast look/feel like the OST toast.
- Future add-ons: moon phase and weather report as small icons in the toast.

Notes

- Keep the toast compact; prefer icons to long text.
- Save config per-world or global; defer until core is done.

This file captures the initial design requirements for reference while building features.
