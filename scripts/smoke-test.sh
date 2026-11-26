#!/usr/bin/env bash
# Verify live health, readiness, version metadata, and Prometheus metrics for staging or production.
# Usage: ./scripts/smoke-test.sh <staging|production> [expected-git-sha]
set -euo pipefail

TARGET="${1:-staging}"
EXPECTED_SHA="${2:-}"
BASE_URL="${BASE_URL:-https://localhost}"
PREFIX="staging"
if [ "$TARGET" = "production" ]; then
  PREFIX="prod"
fi

URL="${BASE_URL}/${PREFIX}"

for i in $(seq 1 20); do
  if curl -kfsS "${URL}/healthz" | grep -q '"status": "ok"'; then
    break
  fi
  if [ "$i" -eq 20 ]; then
    echo "ERROR: Timed out waiting for ${URL}/healthz" >&2
    exit 1
  fi
  sleep 2
done

curl -kfsS "${URL}/readyz" | grep -q '"status": "ready"'
curl -kfsS "${URL}/metrics" | grep -q "probe_requests_total"
VERSION_JSON="$(curl -kfsS "${URL}/version")"
echo "Verified ${TARGET}: ${VERSION_JSON}"

if [ -n "$EXPECTED_SHA" ] && ! echo "$VERSION_JSON" | grep -q "$EXPECTED_SHA"; then
  echo "ERROR: Expected git_sha '${EXPECTED_SHA}' not found in /version response." >&2
  exit 1
fi
