# CI Plan

Scope: Build and test Fabric Loom multi-mods, remap jars, and publish artifacts per commit/PR.

Jobs
- Build (Ubuntu, Java 17/21 matrix):
  - Checkout repo
  - Setup Temurin JDK (matrix)
  - Use gradle/gradle-build-action with Gradle 8.10.2
  - Run `build` then `remapJar` for all modules
  - Upload `mods/*/build/libs/*.jar` as artifacts (exclude sources)

Triggers
- `push` to main and feature branches
- `pull_request` to main
- Manual `workflow_dispatch`

Caching
- gradle-build-action handles Gradle and dependency caching automatically.

Security
- Validate wrapper if present via wrapper-validation action (optional, gated on file existing).

Future Enhancements
- Add code style/lint jobs if we introduce static checks.
- Add release workflow to attach jars to GitHub Releases on tags (e.g., `beautiful_day_counter-vX.Y.Z`).
