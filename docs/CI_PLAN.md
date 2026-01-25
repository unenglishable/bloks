# CI Plan

Scope: Build/test Fabric Loom multi-mods, remap jars, and publish per-module releases.

Build job (ci.yml)
- Ubuntu; Java matrix [17, 21] to ensure compatibility.
- Steps: checkout → setup-java → gradle build → remapJar → upload jars.

Release (release.yml)
- Trigger: tag push `<modid>-v<semver>` (legacy path or manual tags).
- Steps: parse tag → build only module → create GitHub Release → upload jars.

Publish with semantic-release (publish-mod.yml)
- Trigger: manual `workflow_dispatch` with `modid`.
- Steps:
  - Run semantic-release in `mods/<modid>` to compute next version from Angular commits,
    update `gradle.properties`, generate changelog, tag `<modid>-vX.Y.Z`, and create a draft release.
  - Build + remap with Java 21 and upload jars to that release.
  - Optional: publish to Modrinth/CurseForge via `mc-publish` when tokens and project IDs are set.

Versioning policy
- Conventional (Angular) commits drive semantic versioning.
- Per-module versions live in `mods/<modid>/gradle.properties`.
- Java toolchain set to 21 in modules; CI also tests Java 17.

Security & caching
- Gradle caches via gradle-build-action.
- Wrapper validation if wrapper files exist.
