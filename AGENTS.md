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

Tooling via asdf

- Prefer asdf for toolchains. See `docs/TOOLING.md`.
- If the Gradle wrapper is missing locally, generate it with `scripts/bootstrap-wrapper.sh`
  (requires a locally installed Gradle via asdf) or use `gradle` directly.
- Versions source of truth: `.tool-versions`. Keep CI workflows (Gradle/Node) aligned with it.

## Dependencies & Tooling

- Apply `fabric-loom` in mod subprojects.
- Pin versions in `libs.versions.toml`:
  - `net.fabricmc:fabric-loader`
  - `net.fabricmc.fabric-api:fabric-api`
  - Minecraft version (compat matrix per mod as needed)
- Use Java 17 (or 21 for newer MC) via Gradle toolchains.

## External Documentation (Context7)

- We have access to Context7 for up-to-date library docs and examples. Use it whenever standard docs
  are needed rather than guessing APIs.
- Workflow: call `context7__resolve-library-id` with the library name (unless the user supplies an
  ID like `/org/project`), then query via `context7__query-docs`.
- Respect the limit of three Context7 calls per user request (resolve + two queries typically
  suffices). If docs still aren't found, fall back to existing knowledge and note the gap.
- Never include secrets in Context7 queries; provide only the necessary technical question.
- When API behavior is unclear (e.g., method parameters), prefer pulling the reference via Context7
  instead of guessing; better to ask than to ship a build break.

## Coding Style & Naming Conventions

- Indentation: 4 spaces (Java/Kotlin), 2 spaces for JSON.
- Packages `com.example.<modid>`; classes `PascalCase`; methods/fields `camelCase`.
- `modid` is lowercase alphanumeric/underscores (e.g., `teleport_plus`).
- `fabric.mod.json` keys: `id`, `name`, `version`, `entrypoints` (`main`, `client`), `depends`
  (`fabricloader`, `fabric`, `minecraft`), `mixins`.

## Shared Code Strategy

- Preferred: `implementation(project(":libs:common"))` for shared logic; keep code
  platform-agnostic.
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
- No secrets in repo; use Gradle properties or `.env.example`. Use Git LFS for large assets if
  needed.

## Agent Commit Workflow

- After each change, run a single command that stages and commits; rely on the harness to prompt for
  approval.
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
  - `mods/<modid>/README.md` — user-facing docs (what it does, how to use, config, fun facts).
  - `mods/<modid>/DEV.md` — developer docs (architecture, local dev, technical details).
- Update CI/publishing docs when workflows change:
  - `docs/CI_PLAN.md`, `docs/PUBLISHING.md`, `docs/PLATFORM_SETUP.md`.
- Reflect version bumps mentioned in docs with `gradle/libs.versions.toml` changes.
- Use `docs:` commits for documentation-only changes and keep them separate from code.
- Implementation guide: see `docs/IMPLEMENTATION_GUIDE.md` for technology-agnostic steps to add new
  mods and restart work in new AI sessions. When altering `PLAN.md`, prefer deprecating lines
  (prefix `Deprecated:` with a date) rather than deleting.
- Any time you touch Markdown, run `npx -y prettier@3.2.5 --write "**/*.md"` (or its check mode) and
  commit the resulting changes as part of the same task. Prettier fixes are always acceptable.

## Task Tracking with Beads (`bd`)

- This repo is initialized with [Beads](https://github.com/steveyegge/beads); all planning lives in
  `.beads/`. Use the `bd` CLI for every task instead of free-form notes.
- Core commands:
  - `bd create "Title" -p <priority>` — create a task (P0 = urgent, P3 = low).
  - `bd ready` — list tasks whose blockers are cleared.
  - `bd show <id>` — inspect task details/history.
  - `bd dep add <child> <parent>` — express blockers/relationships.
  - `bd update <id> --notes "..."` — amend description/notes (avoids interactive editors).
  - `bd close <id> --reason "Completed"` — mark tasks finished when work lands.
- Do **not** use `bd edit` (it launches `$EDITOR`). Stick to `bd update` flags for non-interactive
  changes.
- If `bd` reports setup issues, run `bd doctor --fix`. Use `bd sync` before pushing so issues export
  to `.beads/issues.jsonl`.
- For local scratch work, you can point `BEADS_DB=/tmp/foo.db bd ...`, but all shared planning must
  be recorded in this repo’s `.beads/`.

Documentation boundaries

- Top-level `README.md` is general-purpose: repo layout, CI/release, generic dev/testing commands.
- Per-mod specifics (usage, config, dev notes) live under that mod’s directory.

## External Data & Version Pinning

- Do not claim knowledge of “latest” dependency/plugin versions if network access is unavailable.
- When a version is needed, ask the user to provide authoritative metadata (e.g.,
  `maven-metadata.xml`) via `curl` and paste/upload, then pin the exact version in
  `gradle/libs.versions.toml`.
- Prefer mapping plugin IDs to modules in `settings.gradle.kts` (`pluginManagement` +
  `resolutionStrategy`) so versions resolve from the correct repository (e.g., Fabric Maven for
  Loom).
- Add the required repositories explicitly (e.g., Fabric, TerraformersMC) and keep them scoped in
  `pluginManagement` (for plugins) and project `repositories` (for libraries).
- Treat `.tool-versions` as the source of truth for Java/Gradle/Node; align CI/workflows with it.

## Landing the Plane (Session Completion)

**When ending a work session**, you MUST complete ALL steps below. Work is NOT complete until
`git push` succeeds.

**MANDATORY WORKFLOW:**

1. **File issues for remaining work** - Create issues for anything that needs follow-up
2. **Run quality gates** (if code changed) - Tests, linters, builds
3. **Update issue status** - Close finished work, update in-progress items
4. **PUSH TO REMOTE** - This is MANDATORY:
   ```bash
   git pull --rebase
   bd sync
   git push
   git status  # MUST show "up to date with origin"
   ```
5. **Clean up** - Clear stashes, prune remote branches
6. **Verify** - All changes committed AND pushed
7. **Hand off** - Provide context for next session

**CRITICAL RULES:**

- Work is NOT complete until `git push` succeeds
- NEVER stop before pushing - that leaves work stranded locally
- NEVER say "ready to push when you are" - YOU must push
- If push fails, resolve and retry until it succeeds
