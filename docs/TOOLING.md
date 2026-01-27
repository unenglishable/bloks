# Tooling — asdf setup

Use asdf to manage toolchains consistently across machines.

## Plugins

- Install plugins:
  - `asdf plugin add java https://github.com/halcyon/asdf-java.git`
  - `asdf plugin add gradle https://github.com/rfrancis/asdf-gradle.git`
  - `asdf plugin add nodejs https://github.com/asdf-vm/asdf-nodejs.git`

## Versions

- Source of truth: `.tool-versions` in the repo root.
- Ensure it specifies Java 21, Gradle 8.x, and Node 20.x (for tooling).
  - Install via asdf: `asdf install` (after plugins are added)

## Wrapper (optional but recommended)

- Generate a Gradle wrapper locally (once):
  - Ensure Gradle is installed via asdf, then run:
    - `scripts/bootstrap-wrapper.sh` (reads the Gradle version from `.tool-versions`)
- After the wrapper exists, use `./gradlew` instead of `gradle`.
- You may commit wrapper files for convenience; CI does not require them but supports either path.

## Notes

- Ensure `java -version` reports 21.x.
- If Prettier is not installed globally, use `npx` (CI uses `npx`).
- npx requires a writable npm cache inside the asdf Node.js install. Run
  `scripts/bootstrap-npm-cache.sh` (once per Node version) so the `<install>/.npm` directory exists;
  the script reads `.tool-versions` automatically but also accepts an explicit version argument.
