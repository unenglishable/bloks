#!/usr/bin/env bash
set -euo pipefail

# Bootstrap the Gradle wrapper locally. Requires a system Gradle installation.
# Usage: scripts/bootstrap-wrapper.sh [GRADLE_VERSION] [--force]

FORCE_BOOTSTRAP=0
USER_VERSION=""
for arg in "$@"; do
  case "$arg" in
    --force|--force-bootstrap) FORCE_BOOTSTRAP=1 ;;
    *) USER_VERSION="$arg" ;;
  esac
done

if [ -f .tool-versions ]; then
  VERSION_FROM_FILE=$(awk '/^gradle[[:space:]]/ { print $2; exit }' .tool-versions)
fi
VERSION="${USER_VERSION:-${VERSION_FROM_FILE:-8.14.4}}"
echo "Bootstrapping Gradle wrapper ${VERSION}..."

# Verify system Gradle version is recent enough to evaluate the build (Loom requires Gradle 8.x).
if command -v gradle >/dev/null 2>&1; then
  # Robustly parse version from "Gradle X.Y[.Z]" line; works with BSD awk
  GV=$(gradle --version 2>/dev/null | awk '/^Gradle /{print $2; exit}')
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


if [ "$FORCE_BOOTSTRAP" -eq 0 ]; then
  echo "Attempting in-repo wrapper generation..."
  if gradle wrapper --gradle-version "${VERSION}" --distribution-type bin >/dev/null 2>&1; then
    echo "Wrapper created by configuring current build."
    echo "You can now use ./gradlew with Gradle ${VERSION}."
    exit 0
  fi
fi

echo "Direct wrapper task failed (likely due to plugin configuration). Using bootstrap fallback..."
BOOT_DIR=".gradle-wrapper-bootstrap"
mkdir -p "${BOOT_DIR}"
cat > "${BOOT_DIR}/settings.gradle.kts" <<'EOF'
rootProject.name = "wrapper-bootstrap"
EOF
cat > "${BOOT_DIR}/build.gradle.kts" <<EOF
tasks.register("noop") {}
EOF

(
  cd "${BOOT_DIR}"
  gradle wrapper --gradle-version "${VERSION}" --distribution-type bin
)

cp -f "${BOOT_DIR}/gradlew" ./gradlew
cp -f "${BOOT_DIR}/gradlew.bat" ./gradlew.bat
mkdir -p gradle/wrapper
cp -f "${BOOT_DIR}/gradle/wrapper/gradle-wrapper.jar" gradle/wrapper/gradle-wrapper.jar
cp -f "${BOOT_DIR}/gradle/wrapper/gradle-wrapper.properties" gradle/wrapper/gradle-wrapper.properties

echo "Wrapper files copied from bootstrap project. Cleaning up..."
rm -rf "${BOOT_DIR}"
echo "Done. You can now use ./gradlew with Gradle ${VERSION}."
