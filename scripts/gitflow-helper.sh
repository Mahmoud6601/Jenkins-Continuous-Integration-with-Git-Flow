#!/usr/bin/env bash
# Local Git Flow branch helper enforcing naming policy before pushing.
set -euo pipefail

CMD="${1:-}"
NAME="${2:-}"

case "$CMD" in
  validate-branch)
    BRANCH="${NAME:-$(git rev-parse --abbrev-ref HEAD)}"
    if [[ "$BRANCH" =~ ^(main|develop|feature/[a-z0-9][a-z0-9._-]*|bugfix/[a-z0-9][a-z0-9._-]*|release/v[0-9]+\.[0-9]+\.[0-9]+|hotfix/v[0-9]+\.[0-9]+\.[0-9]+)$ ]]; then
      echo "OK: Branch '${BRANCH}' complies with Git Flow policy."
    else
      echo "ERROR: Branch '${BRANCH}' violates Git Flow naming policy." >&2
      exit 1
    fi
    ;;
  start-feature)
    : "${NAME:?Usage: $0 start-feature <short-slug>}"
    git checkout develop && git checkout -b "feature/${NAME}"
    ;;
  start-release)
    : "${NAME:?Usage: $0 start-release <X.Y.Z>}"
    git checkout develop && git checkout -b "release/v${NAME}"
    ;;
  start-hotfix)
    : "${NAME:?Usage: $0 start-hotfix <X.Y.Z>}"
    git checkout main && git checkout -b "hotfix/v${NAME}"
    ;;
  *)
    echo "Usage: $0 <validate-branch [name]|start-feature <slug>|start-release <X.Y.Z>|start-hotfix <X.Y.Z>>" >&2
    exit 1
    ;;
esac
