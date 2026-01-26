# Platform Setup — CurseForge and Modrinth

This guide explains how to create publisher accounts, projects, and API tokens for both platforms,
then wire them into GitHub Actions for publishing.

## CurseForge (Creators)

- Create/sign in to your CurseForge creator account.
- Create a Minecraft project for your mod and note the numeric Project ID.
- Generate an API token in the CurseForge console (Creators → Account/Settings → API Tokens).
  - Token must allow uploading files for your project.
- Add to GitHub:
  - Repository Variable: `CURSEFORGE_PROJECT_ID` = your numeric ID
  - Repository Secret: `CURSEFORGE_TOKEN` = your API token
- Verification: our `publish-mod.yml` will publish when both ID and token are present.

## Modrinth

- Create/sign in to your Modrinth account.
- Create a project for your mod and note the Project ID or slug.
- Create a token in Account Settings → Applications (scope: project/file upload).
- Add to GitHub:
  - Repository Variable: `MODRINTH_PROJECT_ID` = your project ID/slug
  - Repository Secret: `MODRINTH_TOKEN` = your API token

## GitHub configuration tips

- Using GitHub CLI:
  - `gh variable set CURSEFORGE_PROJECT_ID -b "123456"`
  - `gh secret set CURSEFORGE_TOKEN` (paste token when prompted)
  - `gh variable set MODRINTH_PROJECT_ID -b "your-project-id"`
  - `gh secret set MODRINTH_TOKEN`
- Alternatively, set them in Settings → Secrets and variables → Actions.

## Publishing workflow usage

- Trigger `Publish Mod (Semantic Release)` and pass `modid` (e.g., `beautiful_day_counter`).
- The workflow bumps the version, tags `<modid>-vX.Y.Z`, builds/remaps jars (Java 21), creates a
  GitHub Release, and publishes to Modrinth/CurseForge when credentials are set.
