#!/usr/bin/env bash
set -euo pipefail

# Ensures the asdf-managed Node.js installation has a writable ~/.npm directory.
# npx checks for <install>/.npm before reading project .npmrc; missing directories,
# especially on sandboxed machines, cause ENOENT errors before the repo cache is used.

if ! command -v asdf >/dev/null 2>&1; then
  echo "asdf is required but was not found in PATH." >&2
  exit 1
fi

detect_node_version() {
  if [[ $# -gt 0 && -n "${1:-}" ]]; then
    echo "$1"
    return
  fi
  local current
  current="$(asdf current nodejs 2>/dev/null | awk 'NR==1 { print $2 }')"
  if [[ -n "$current" && "$current" != "system" ]]; then
    echo "$current"
    return
  fi
  local from_tool_versions
  from_tool_versions="$(grep -E '^nodejs ' .tool-versions 2>/dev/null | awk '{ print $2 }' | head -n1)"
  if [[ -n "$from_tool_versions" ]]; then
    echo "$from_tool_versions"
    return
  fi
  echo ""
}

NODE_VERSION="$(detect_node_version "${1:-}")"

if [[ -z "$NODE_VERSION" ]]; then
  echo "Unable to determine nodejs version. Pass it explicitly (e.g., 20.20.0)." >&2
  exit 1
fi

NODE_DIR="$(asdf where nodejs "$NODE_VERSION" 2>/dev/null || true)"
if [[ -z "$NODE_DIR" ]]; then
  echo "Node.js $NODE_VERSION is not installed via asdf. Run 'asdf install nodejs $NODE_VERSION' first." >&2
  exit 1
fi

mkdir -p "$NODE_DIR/.npm/_npx"
echo "Ensured npm cache exists at $NODE_DIR/.npm"
