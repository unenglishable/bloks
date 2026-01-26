#!/usr/bin/env bash
set -euo pipefail

# Bootstrap the Gradle wrapper locally. Requires a system Gradle installation.
# Usage: scripts/bootstrap-wrapper.sh [GRADLE_VERSION]

VERSION="${1:-8.10.2}"
echo "Bootstrapping Gradle wrapper ${VERSION}..."

# Verify system Gradle version is recent enough to evaluate the build (Loom requires Gradle 8.x).
if command -v gradle >/dev/null 2>&1; then
  GV=$(gradle -v | sed -n 's/^Gradle \([0-9][0-9]*\.[0-9][0-9]*\(\.[0-9][0-9]*\)\?\).*/\1/p' | head -n1)
  echo "Detected system Gradle: ${GV:-unknown}"
  # Compare major.minor numerically (best-effort); require >= 8.0
  MAJOR=${GV%%.*}
  if [ -z "${MAJOR}" ] || [ "${MAJOR}" -lt 8 ]; then
    echo "Error: System Gradle must be >= 8.x to evaluate this build (Fabric Loom plugin)." >&2
    echo "Install Gradle ${VERSION} via asdf and retry: asdf install gradle ${VERSION}" >&2
    exit 1
  fi
else
  echo "Error: gradle not found in PATH. Install Gradle ${VERSION} (e.g., via asdf) and retry." >&2
  exit 1
fi

gradle wrapper --gradle-version "${VERSION}" --distribution-type bin

echo "Wrapper created. You can now use ./gradlew with Gradle ${VERSION}."
