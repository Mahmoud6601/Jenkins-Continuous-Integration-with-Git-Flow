#!/usr/bin/env bash
# Instant 1-command rollback to the previous immutable image tag (or an explicit tag).
# Usage: ./scripts/rollback.sh <staging|production> [explicit-tag]
set -euo pipefail
cd "$(dirname "$0")/.."

TARGET="${1:-}"
ROLLBACK_TAG="${2:-}"

if [[ ! "$TARGET" =~ ^(staging|production)$ ]]; then
  echo "Usage: $0 <staging|production> [image-tag]" >&2
  exit 1
fi

PREV_FILE=".state/${TARGET}.previous"
if [ -z "$ROLLBACK_TAG" ]; then
  if [ ! -f "$PREV_FILE" ]; then
    echo "ERROR: No previous tag recorded in ${PREV_FILE}. Pass an explicit tag." >&2
    exit 1
  fi
  ROLLBACK_TAG="$(cat "$PREV_FILE")"
fi

echo "Rolling back '${TARGET}' to immutable tag '${ROLLBACK_TAG}'..."
if [ "$TARGET" = "staging" ]; then
  STAGING_IMAGE_TAG="$ROLLBACK_TAG" docker compose up -d --no-deps payload-staging
else
  PROD_IMAGE_TAG="$ROLLBACK_TAG" docker compose up -d --no-deps payload-prod
fi

echo "$ROLLBACK_TAG" > ".state/${TARGET}.active"
bash ./scripts/smoke-test.sh "$TARGET"
echo "Rollback complete for '${TARGET}' -> '${ROLLBACK_TAG}'."
