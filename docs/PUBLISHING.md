# Publishing

This monorepo publishes each Fabric mod independently via tags and GitHub Releases.

Tagging scheme

- Format: `<modid>-v<semver>` (examples: `beautiful_day_counter-v0.1.0`).
- The workflow builds only the tagged mod and attaches its remapped JAR to the release.

Per‑module versioning

- Each mod defines its own version in `mods/<modid>/gradle.properties` (e.g., `version=0.1.0`).
- Gradle uses this property as `project.version` for the module.

Release workflows

- Tag-driven: `.github/workflows/release.yml` builds on tags `<modid>-v*` and uploads jars.
- Semantic-release (recommended): `.github/workflows/publish-mod.yml` runs on demand with input
  `modid`, computes the next version from commit history, updates the module’s `gradle.properties`,
  creates the tag and GitHub Release, builds/remaps jars, and uploads artifacts.

Platform publishing (optional)

- Modrinth: set `MODRINTH_PROJECT_ID` (repo variable) and `MODRINTH_TOKEN` (secret).
- CurseForge: set `CURSEFORGE_PROJECT_ID` (repo variable) and `CURSEFORGE_TOKEN` (secret).
- The `publish-mod.yml` workflow uses `mc-publish` to publish when IDs and tokens are present.

Developer usage

- Recommend: trigger `Publish Mod (Semantic Release)` workflow with `modid` to auto-bump version,
  tag, and publish artifacts (and optionally to platforms).
- Alternative: manual tag and push (`<modid>-vX.Y.Z`) to trigger the tag-driven release workflow.
