# Beautiful Day Counter — Bring-up Lessons

This note captures every issue we hit while getting the mod playable and getting CI/QA green. Future
agents should skim this before touching the code so we do not repeat the same mistakes.

## Build & Tooling

- **SpotBugs report DSL (build.gradle.kts:47)**

  - _Issues:_ `fix(build): configure spotbugs reports…` and `fix(build): ensure spotbugs…` were
    needed because the Kotlin DSL does not expose `reports.html/xml/sarif` like Groovy. Accessing
    them caused script compilation errors.
  - _Fix:_ Iterate `reports.configureEach { … }`, enable only SARIF, and set the output via
    `rootProject.layout.projectDirectory.file(...)`.
  - _Prevent:_ When copying Groovy snippets, check the Kotlin API (`SpotBugsTask#getReports()`).

- **SARIF aggregation + CI upload**

  - _Issues:_ Commits `build(spotbugs): switch to SARIF…` and `ci(spotbugs): upload SARIF…` were
    added after SpotBugs generated scattered HTML.
  - _Fix:_ Write every report into `build/reports/spotbugs/` and upload in CI so GitHub annotates
    findings.
  - _Prevent:_ Plan report formats + locations before adding analyzers.

- **JUnit runtime missing (`mods/beautiful_day_counter/build.gradle.kts:26`)**

  - _Issues:_ `build(test): add JUnit Jupiter engine` and `build(test): add JUnit Platform launcher`
    landed after tests failed to execute.
  - _Fix:_ Add both `testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")` and the launcher
    dependency.
  - _Prevent:_ Always add runtime engine + launcher whenever we add JUnit tests.

- **Node/npm cache failures (`.npmrc`, `scripts/bootstrap-npm-cache.sh`)**

  - _Issues:_ `build(tooling): pin npm cache…` and `docs(tooling): document npm cache...` came after
    `npx prettier` couldn’t write `~/.npm`.
  - _Fix:_ Route npm cache into `./.npm-cache` and add `scripts/bootstrap-npm-cache.sh` so the asdf
    Node install has `<install>/.npm`.
  - _Prevent:_ Run the bootstrap script after installing any new Node version.

- **Prettier drift (`docs/*.md`, `mods/beautiful_day_counter/*.md`)**

  - _Issues:_ Formatting accumulated until `style(docs): run npx … --write "**/*.md"`.
  - _Fix:_ Run Prettier in write mode any time docs change; keep `.prettierignore` updated.
  - _Prevent:_ Integrate `npx prettier --check` locally before pushing.

- **Version alignment**

  - _Issues:_ Multiple commits (`build(tool-versions)`, `ci/build`, `build(loom)`, `build(repos)`)
    track bumps to Gradle, Loom, yarn mappings, Mod Menu, and plugin repositories.
  - _Fix:_ Centralize versions in `.tool-versions` and `gradle/libs.versions.toml` and keep CI
    workflows synced.
  - _Prevent:_ Update tooling + docs atomically with any version bump.

- **Gradle wrapper bootstrap script (`scripts/bootstrap-wrapper.sh`)**
  - _Issues:_ A long chain (`scripts/bootstrap-wrapper.sh` commits culminating in
    `refactor(scripts/bootstrap): update ...`) shows how easy it is to brick local dev when the
    wrapper is absent or bootstrapped with the wrong system Gradle.
  - _Fix:_ Always run `scripts/bootstrap-wrapper.sh` via an asdf-installed Gradle that meets the
    minimum version (`chore(scripts): guard wrapper bootstrap with system Gradle >= 8.x`) and keep
    the script up to date before regenerating the wrapper.
  - _Prevent:_ Never call `gradle wrapper` manually; rely on the script so the repo stays portable.

## Static Analysis & Style

- **Spotless import order (`mods/beautiful_day_counter/src/...`)**

  - _Issue:_ Commit `chore(beautiful_day_counter): satisfy Spotless import order/spacing` cleaned up
    failing format checks.
  - _Fix:_ Run `./gradlew spotlessApply` after touching Java files.
  - _Prevent:_ Configure IDE formatting to Google Java style so Spotless has nothing to fix.

- **Checkstyle braces (`mods/beautiful_day_counter/src/client/java/.../ConfigScreen.java`)**

  - _Issue:_ Three commits (`style(checkstyle): ... add braces`) were needed because single-line
    `if` statements violated the root Checkstyle config.
  - _Fix:_ Always use braces, even for single statements.
  - _Prevent:_ Turn on the IDE rule for “require braces”.

- **SpotBugs warnings and annotations (`mods/beautiful_day_counter/src/client/java/...`)**
  - _Issues:_ `build(beautiful_day_counter): add SpotBugs annotations dependency`,
    `fix(beautiful_day_counter): resolve SpotBugs warnings`, and `refactor(bdc): some fixes...`
    addressed singleton exposure and nullability issues.
  - _Fix:_ Depend on `spotbugs-annotations` and add targeted `@SuppressFBWarnings` plus code tweaks
    (e.g., `ClientState.get()` returns).
  - _Prevent:_ Run `./gradlew :mods:beautiful_day_counter:spotbugsMain` often; read the report
    before pushing.

## Gameplay / Client Bring-up

- **Toast API churn (`mods/.../client/ui/DayToast.java`)**

  - _Issues:_ The series `fix(client): add Yarn-compatible DayToast ...` through
    `fix(client): ensure HUD shows on first join` highlight how Yarn’s `Toast` interface changed
    (different `draw`, `update`, `getVisibility` signatures).
  - _Fix:_ Implement a custom toast matching the exact Yarn version, with explicit clock icon
    rendering (`DayToast.java:1`).
  - _Prevent:_ Always check the mapped interface in the targeted Minecraft version before porting UI
    code from other repos.

- **Networking payload API (`mods/.../BeautifulDayCounter.java` & client)**

  - _Issues:_ Commits `fix(network): register typed payload`, `fix(network): use ByteBuf-based...`,
    `refactor(network): remove legacy Packets.java`, and `feat(network): add S2C day-change packet`
    chart the move to Fabric’s typed payloads. Crashes happened when the registry mismatched.
  - _Fix:_ Define `DayChangePayload` with `PacketCodec`, register in both Server and Client reload
    paths, and fall back to client-side calculation (`BeautifulDayCounterClient.java:23`).
  - _Prevent:_ When Loom/Yarn updates, re-verify packet registration and ensure IDs match.

- **HUD toggle + fallback (`mods/.../client/BeautifulDayCounterClient.java`)**

  - _Issues:_ `feat(client): restore keybinding`, `fix(client): ensure HUD shows on first join`, and
    later commits ensured the HUD respects F1, persists label, and initializes day count to avoid
    catch-up toasts.
  - _Fix:_ Keep a `toggleKey` instance field, sync `ClientState` with config on init, and compute
    the first day from the client world when no packet arrived.
  - _Prevent:_ Whenever we add a HUD, build both server-authoritative and client fallbacks.

- **Toast spam / concurrency (`mods/.../client/ui/Toasts.java`)**
  - _Issue:_ `fix(toast): prevent infinite queueing...` addressed multiple toasts stacking.
  - _Fix:_ Track `ClientState.toastActive` and bail if one is showing.
  - _Prevent:_ Always guard UI popups with client-side state to avoid concurrency loops.

## UI / Config Lessons

- **Localized strings (`mods/.../lang/en_us.json`, `ConfigScreen.java`)**

  - _Issues:_ `feat(i18n+test)` and `fix(i18n)` commits tracked down missing `Text.translatable`
    calls and JSON syntax errors.
  - _Fix:_ Use `Text.translatable` everywhere, keep lang files sorted, and run Spotless on JSON.
  - _Prevent:_ When adding UI copy, update lang files immediately.

- **Config screen UX**

  - _Issues:_ Commits `fix(ui): add TextFieldWidget suggestion`, `fix(ui): render caption...`,
    `feat(config-ui): center vertically`, `fix(ui): match Screen.resize signature`, etc., show how
    easy it is to regress the config screen when mappings change.
  - _Fix:_ Encapsulate all layout logic inside `ConfigScreen.relayout()` and rely on constants for
    spacing. Keep `resize(int width, int height)` signature in sync with Yarn.
  - _Prevent:_ After GUI changes, launch the dev client
    (`./gradlew :mods:beautiful_day_counter:runClient`) and test different resolutions.

- **Config persistence and live preview (`Config.java`, `ClientState.java`)**
  - _Issues:_ `feat(config): add Toast enable/disable`,
    `a9109d1 fix(config): make toast toggle live`, etc., ensure toggles update `ClientState`.
  - _Fix:_ For every config field, store it in JSON _and_ propagate to `ClientState` before closing
    the screen (see `ConfigScreen.java:70`).
  - _Prevent:_ When adding config options, extend `Config`, `ClientState`, and UI simultaneously.

## Process / Workflow

- **Docs & tooling guides are required reading**

  - Commits `docs: add implementation guide`, `docs(readme): add repo overview`,
    `docs(agents): add documentation maintenance...`, and `docs(tooling): add asdf setup guide` were
    added because agents skipped local setup/context. Always study `AGENTS.md`,
    `docs/IMPLEMENTATION_GUIDE.md`, and `docs/TOOLING.md` at session start.

- **Commit commands must actually run**

  - `docs(agents): require running commit commands (no proposal step)` landed after agents tried to
    ask for approval _before_ staging/committing. Always run `git add ... && git commit ...` so the
    harness can prompt properly; never propose commands.

- **Doc boundaries + module maintenance**

  - Commits `docs(beautiful_day_counter): separate user and developer docs`,
    `docs(agents): add documentation maintenance and linking rules`, and
    `docs(readme): crosslink ...` came from mixing user-facing and developer notes. Keep repo-level
    docs generic and put mod-specific details under `mods/<id>/`.

- **Docs vs. code split**

  - The repo enforces doc updates alongside behavior changes (see `docs(beautiful_day_counter): ...`
    commits and AGENTS.md). Always keep PLAN/DEV/README files in sync so future contributors know
    status.

- **Conventional commits**
  - Commits are scoped (e.g., `fix(build)`, `feat(client)`). Keep using that format so the history
    reads like a troubleshooting diary future agents can learn from.

## CI & Release

- **CI parity with .tool-versions**

  - Commits `ci/build: align Gradle to 8.14.4`, `ci: align Gradle/Node with .tool-versions`, and
    later bumps show we repeatedly broke CI by upgrading tooling locally first. Whenever
    `.tool-versions` changes, update CI workflows (`.github/workflows/`) in the same PR.

- **Quality gates in CI**

  - `ci: run Spotless, Checkstyle, and SpotBugs` ensures formatting and analysis run server-side.
    Local code must pass `./gradlew spotlessCheck checkstyleMain spotbugsMain` before pushing.

- **Release automation expectations**
  - Commits `ci: add GitHub Actions build`, `ci(release): per-mod tag-driven releases`,
    `ci: add semantic-release publish workflow`, and `docs(platforms): add setup guide...`
    illustrate that releases assume semantic commits, module tags, and configured tokens. Keep
    changelogs/docs accurate so semantic-release can cut versions without manual intervention.

Review this file at the start of each session to refresh what already broke and how it was fixed.
