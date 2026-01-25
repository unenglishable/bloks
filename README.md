# Bloks Minecraft Mods Monorepo

We (you + this AI agent) are building Minecraft Java mods with Fabric (Loom) in a Gradle multi-module repo. Each mod lives under `mods/<modid>` and can be built and released independently.

## Structure
- Root Gradle
  - `settings.gradle.kts` includes modules under `mods/`
  - `build.gradle.kts` shared repos/config
  - `gradle/libs.versions.toml` MC/loader/API/loom versions
- Mods
  - `mods/<modid>/build.gradle.kts` Loom + deps (Java 21)
  - `mods/<modid>/src/main/...` code; `fabric.mod.json` in resources
  - `mods/<modid>/src/client/...` client-only code
  - `mods/<modid>/gradle.properties` per-module version
  - `mods/<modid>/PLAN.md` and `DESIGN.md` plan + design prompts
- Shared & CI
  - `scripts/bootstrap-wrapper.sh` create local Gradle wrapper
  - `.github/workflows/ci.yml` PR/branch build + remapJar (Java 17, 21)
  - `.github/workflows/release.yml` tag-driven release `<modid>-v*` (Java 21)
  - `.github/workflows/publish-mod.yml` semantic-release per mod (manual)
  - Docs: `AGENTS.md`, `docs/CI_PLAN.md`, `docs/PUBLISHING.md`, `docs/PLATFORM_SETUP.md`

## CI & Releases
- CI builds on pushes/PRs and uploads jars from `mods/*/build/libs`.
- Per-module release paths:
  - Tags: push `<modid>-vX.Y.Z` to build that module and attach jars to a GitHub Release.
  - Semantic Release: run Publish Mod (Semantic Release) with `modid` to bump version from commits,
    tag, build (Java 21), upload jars, and optionally publish to Modrinth/CurseForge.
- Token setup: see `docs/PLATFORM_SETUP.md`. Publishing details in `docs/PUBLISHING.md`.

## Getting Started (new machine)
1) Install JDK 21 (Temurin). Optional: JDK 17 for matrix testing.
2) Clone repo. Ensure `java -version` shows 21.
3) Optional: `scripts/bootstrap-wrapper.sh 8.10.2` to generate local wrapper.
4) Build: `./gradlew build` (or `gradle build`).
5) Run dev: `./gradlew :mods:<modid>:runClient` or `:runServer`.
6) For publishing, add tokens/IDs as in `docs/PLATFORM_SETUP.md`.

## Conventions
- Conventional Commits (Angular). Split changes by type/scope. Stage explicit files only.
- Java toolchain 21 in modules; CI tests 17/21.
- Mod IDs lowercase with underscores; packages `com.yourorg.<modid>`.

## Current Mods
- `mods/beautiful_day_counter` subtle HUD day counter + new-day toast. See `PLAN.md`.
