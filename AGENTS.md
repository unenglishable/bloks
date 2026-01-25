# Repository Guidelines

## Project Structure & Module Organization
- Gradle multi-module for multiple Fabric mods using Loom.
- Root:
  - `settings.gradle.kts` — includes all modules
  - `build.gradle.kts` — common config (Java toolchain, repos)
  - `gradle/libs.versions.toml` — dependency catalog (loader, Fabric API, MC)
  - `mods/<modid>/` — one subproject per mod
    - `src/main/java|kotlin/...`
    - `src/client/java` (optional, client-only code)
    - `src/main/resources/fabric.mod.json`, `src/main/resources/mixins.<modid>.json`
    - `build.gradle.kts`
  - `libs/common/` — shared utilities
  - `scripts/` — local dev helpers
  - `.github/workflows/` — CI pipelines

## Build, Test, and Development Commands
- Build all modules: `./gradlew build`
- Remap jar for distribution: `./gradlew :mods:<modid>:remapJar`
- Run dev client: `./gradlew :mods:<modid>:runClient`
- Run dev server: `./gradlew :mods:<modid>:runServer`
- Run tests (all/one): `./gradlew test` / `./gradlew :mods:<modid>:test`

## Dependencies & Tooling
- Apply `fabric-loom` in mod subprojects.
- Pin versions in `libs.versions.toml`:
  - `net.fabricmc:fabric-loader`
  - `net.fabricmc.fabric-api:fabric-api`
  - Minecraft version (compat matrix per mod as needed)
- Use Java 17 (or 21 for newer MC) via Gradle toolchains.

## Coding Style & Naming Conventions
- Indentation: 4 spaces (Java/Kotlin), 2 spaces for JSON.
- Packages `com.example.<modid>`; classes `PascalCase`; methods/fields `camelCase`.
- `modid` is lowercase alphanumeric/underscores (e.g., `teleport_plus`).
- `fabric.mod.json` keys: `id`, `name`, `version`, `entrypoints` (`main`, `client`), `depends` (`fabricloader`, `fabric`, `minecraft`), `mixins`.

## Shared Code Strategy
- Preferred: `implementation(project(":libs:common"))` for shared logic; keep code platform-agnostic.
- Avoid shading unless necessary; Loom remapping handles mod jars.

## Testing Guidelines
- Unit tests: JUnit 5 for pure logic. Mirror sources under `mods/<modid>/src/test/java`.
- In-game behavior: optional Fabric GameTest for integration-like checks.
- Keep tests deterministic (no network); target ~80% coverage where practical.

## Commit & Pull Request Guidelines
- Conventional Commits (`feat:`, `fix:`, `chore:`, `refactor:`, `test:`, `docs:`).
  - Example: `feat(teleport_plus): add /back command`
- PRs: clear description, linked issues (`Closes #123`), test plan, and any breaking changes.

## Security & Repo Hygiene
- Do not commit `run/`, worlds, or server jars. Ignore `build/`, `.gradle/`.
- No secrets in repo; use Gradle properties or `.env.example`. Use Git LFS for large assets if needed.

## Agent Commit Workflow
- After each change, run a single command that stages and commits; rely on the harness to prompt
  for approval.
- Use Angular Conventional Commits. Keep subject ≤100 chars; body lines ≤100 chars.
- Provide a human-readable message via heredoc instead of multiple `-m` flags.
- Stage an explicit list of files only (e.g., `git add path/to/file1 path/to/file2`).
- Never use `git add -A` or `git add .`, and never use `git commit -a`/`git commit -A`.
- Split logically distinct changes into separate commits by type/scope. Do not mix types in one
  commit (e.g., keep `build:` separate from `docs:` or `ci:`). Run multiple commits sequentially.
- Example (adjust scope and files as needed):
  
  ```
  git add AGENTS.md && git commit -F - <<'COMMIT_MSG'
  docs(agents): add Fabric Loom guidelines and agent commit workflow

  Document Fabric (Loom) multi-mod structure, commands, dependencies, and testing conventions.
  Add agent commit workflow using Angular Conventional Commits, with here-doc message formatting and
  100-character line limits for subject and body.
  COMMIT_MSG
  ```

## Documentation Maintenance
- Keep docs current whenever structure, workflows, or versions change.
- Update crosslinks: ensure README and module docs link to any new/renamed files.
- Edit module docs:
  - `mods/<modid>/PLAN.md` — phases, next steps, testing.
  - `mods/<modid>/DESIGN.md` — product prompts and decisions.
- Update CI/publishing docs when workflows change:
  - `docs/CI_PLAN.md`, `docs/PUBLISHING.md`, `docs/PLATFORM_SETUP.md`.
- Reflect version bumps mentioned in docs with `gradle/libs.versions.toml` changes.
- Use `docs:` commits for documentation-only changes and keep them separate from code.
