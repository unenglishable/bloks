# Bloks Minecraft Mods Monorepo

We (you + this AI agent) are building Minecraft Java mods with Fabric (Loom) in a Gradle multi-module repo. Each mod lives under `mods/<modid>` and can be built and released independently.

## Structure
- Root Gradle
  - [settings.gradle.kts](settings.gradle.kts) includes modules under `mods/`
  - [build.gradle.kts](build.gradle.kts) shared repos/config
  - [gradle/libs.versions.toml](gradle/libs.versions.toml) MC/loader/API/loom versions
- Mods
  - mods/<modid>/
    - [build.gradle.kts](mods/beautiful_day_counter/build.gradle.kts) Loom + deps (Java 21)
    - src layout: `src/main/...` code; [fabric.mod.json](mods/beautiful_day_counter/src/main/resources/fabric.mod.json)
    - `src/client/...` client-only code
    - [gradle.properties](mods/beautiful_day_counter/gradle.properties) per-module version
    - Planning: [PLAN.md](mods/beautiful_day_counter/PLAN.md), [DESIGN.md](mods/beautiful_day_counter/DESIGN.md)
- Shared & CI
  - [scripts/bootstrap-wrapper.sh](scripts/bootstrap-wrapper.sh) create local Gradle wrapper
  - [ci.yml](.github/workflows/ci.yml) PR/branch build + remapJar (Java 17, 21)
  - [release.yml](.github/workflows/release.yml) tag-driven release `<modid>-v*` (Java 21)
  - [publish-mod.yml](.github/workflows/publish-mod.yml) semantic-release per mod (manual)
  - Docs: [AGENTS.md](AGENTS.md), [CI_PLAN.md](docs/CI_PLAN.md), [PUBLISHING.md](docs/PUBLISHING.md), [PLATFORM_SETUP.md](docs/PLATFORM_SETUP.md)

## CI & Releases
- CI builds on pushes/PRs and uploads jars from `mods/*/build/libs`.
- Per-module release paths:
  - Tags: push `<modid>-vX.Y.Z` to build that module and attach jars to a GitHub Release (see [release.yml](.github/workflows/release.yml)).
  - Semantic Release: run Publish Mod (Semantic Release) with `modid` (see [publish-mod.yml](.github/workflows/publish-mod.yml)) to bump version from commits, tag, build (Java 21), upload jars, and optionally publish to Modrinth/CurseForge.
- Token setup: see [docs/PLATFORM_SETUP.md](docs/PLATFORM_SETUP.md). Publishing details in [docs/PUBLISHING.md](docs/PUBLISHING.md).

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
- [mods/beautiful_day_counter](mods/beautiful_day_counter) subtle HUD day counter + new-day toast. See [PLAN.md](mods/beautiful_day_counter/PLAN.md).
