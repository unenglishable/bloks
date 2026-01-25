#!/usr/bin/env bash
set -euo pipefail

# Bootstrap the Gradle wrapper locally. Requires a system Gradle installation.
# Usage: scripts/bootstrap-wrapper.sh [GRADLE_VERSION]

VERSION="${1:-8.10.2}"
echo "Bootstrapping Gradle wrapper ${VERSION}..."

gradle wrapper --gradle-version "${VERSION}" --distribution-type bin

echo "Wrapper created. Consider validating with gradle wrapper --gradle-version ${VERSION}."

