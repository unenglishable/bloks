# Tooling — asdf setup

Use asdf to manage toolchains consistently across machines.

## Plugins
- Install plugins:
  - `asdf plugin add java https://github.com/halcyon/asdf-java.git`
  - `asdf plugin add gradle https://github.com/rfrancis/asdf-gradle.git`
  - `asdf plugin add nodejs https://github.com/asdf-vm/asdf-nodejs.git`

## Versions
- Recommended versions:
  - Java: Temurin 21 (required for Minecraft 1.21.x)
  - Gradle: 8.10.2
  - Node.js: 20.x (for Prettier/semantic-release tooling)
- Example `.tool-versions` (adjust to exact versions available on your system):
  
  ```
  java temurin-21
  gradle 8.10.2
  nodejs 20.11.1
  ```

## Wrapper (optional but recommended)
- Generate a Gradle wrapper locally (once):
  - Ensure Gradle is installed via asdf, then run:
    - `scripts/bootstrap-wrapper.sh 8.10.2`
- After the wrapper exists, use `./gradlew` instead of `gradle`.
- You may commit wrapper files for convenience; CI does not require them but supports either path.

## Notes
- Ensure `java -version` reports 21.x.
- If Prettier is not installed globally, use `npx` (CI uses `npx`).
