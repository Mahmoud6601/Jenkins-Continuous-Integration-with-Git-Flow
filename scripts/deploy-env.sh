#!/usr/bin/env bash
# Deploy an immutable image tag to staging or production and record previous tag for instant rollback.
# Usage: ./scripts/deploy-env.sh <staging|production> <image-tag>
set -euo pipefail
cd "$(dirname "$0")/.."

TARGET="${1:-}"
IMAGE_TAG="${2:-}"

if [[ ! "$TARGET" =~ ^(staging|production)$ ]] || [ -z "$IMAGE_TAG" ]; then
  echo "Usage: $0 <staging|production> <image-tag>" >&2
  exit 1
fi

mkdir -p .state
ACTIVE_FILE=".state/${TARGET}.active"
PREV_FILE=".state/${TARGET}.previous"

if [ -f "$ACTIVE_FILE" ]; then
  cp "$ACTIVE_FILE" "$PREV_FILE"
fi

echo "$IMAGE_TAG" > "$ACTIVE_FILE"

if [ "$TARGET" = "staging" ]; then
  STAGING_IMAGE_TAG="$IMAGE_TAG" docker compose up -d --no-deps payload-staging
else
  PROD_IMAGE_TAG="$IMAGE_TAG" docker compose up -d --no-deps payload-prod
fi

echo "Deployed '${IMAGE_TAG}' to '${TARGET}'."
