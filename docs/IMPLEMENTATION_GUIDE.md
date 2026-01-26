# Implementation Guide — Adding New Mods

This guide captures our repeatable development flow, agnostic of any specific AI agent.

## Module Scaffold
- Create `mods/<modid>/` with:
  - `build.gradle.kts` applying Loom and dependencies
  - `src/main/java|kotlin` and `src/main/resources/fabric.mod.json`
  - Optional `src/client/java` for client-only code
  - `gradle.properties` with `version=X.Y.Z`
  - `PLAN.md` and `DESIGN.md` seeded from a brief

## Dev Flow
- Small, focused commits using Angular Conventional Commits.
- Stage explicit files only; never use `git add -A` or `git commit -a`.
- Keep `build:`, `feat:`, `ci:`, `docs:`, `chore:` changes separate.
- Use heredoc for commit messages and wrap at ≤100 chars/line.

## CI & Release
- CI builds all modules and uploads jars on pushes/PRs.
- Releases per module via:
  - Tags `<modid>-vX.Y.Z`, or
  - Semantic Release workflow (`publish-mod.yml`) with `modid` input.
- Platform publishing via `mc-publish` when tokens/IDs are configured.

## Tokens & Secrets
- See `docs/PLATFORM_SETUP.md` for CurseForge and Modrinth setup.

## Restarting Work (New AI or Session)
- Read `README.md` for structure and commands.
- Read module `README.md`, `PLAN.md`, and `DESIGN.md` to pick up context.
- Follow `AGENTS.md` commit workflow and documentation maintenance rules.
- If CI fails, check `.github/workflows/*.yml` and update docs accordingly.

## Plan Maintenance
- Prefer deprecating lines over deletion in `PLAN.md` (prefix with `Deprecated:` and date).
- Append a “Changes” or “Status” section noting what was completed and what shifted.
- Keep the roadmap updated with the next 1–3 concrete steps.
