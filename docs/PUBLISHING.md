# Publishing

This monorepo publishes each Fabric mod independently via tags and GitHub Releases.

Tagging scheme
- Format: `<modid>-v<semver>` (examples: `beautiful_day_counter-v0.1.0`).
- The workflow builds only the tagged mod and attaches its remapped JAR to the release.

Per‑module versioning
- Each mod defines its own version in `mods/<modid>/gradle.properties` (e.g., `version=0.1.0`).
- Gradle uses this property as `project.version` for the module.

Release workflow
- File: `.github/workflows/release.yml`
- Trigger: push tags matching `*-v*`.
- Steps:
  - Parse `GITHUB_REF_NAME` to extract `<modid>` and `<version>`.
  - Build and remap only `:mods:<modid>`.
  - Create a GitHub Release and upload `mods/<modid>/build/libs/*.jar` artifacts.

Credentials
- Uses the repository’s built‑in `GITHUB_TOKEN` for creating releases.
- Modrinth/CurseForge (optional): add secrets later and integrate a publish step.

Developer usage
- Bump module version: edit `mods/<modid>/gradle.properties`.
- Tag and push: `git tag <modid>-vX.Y.Z && git push origin <modid>-vX.Y.Z`.
- Artifacts appear on the GitHub Release for that tag.
