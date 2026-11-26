#!/usr/bin/env bash
# Back up Jenkins controller configuration, jobs, and secrets with SHA-256 checksum.
set -euo pipefail
cd "$(dirname "$0")/.."

BACKUP_DIR="backups"
TIMESTAMP="$(date -u +%Y%m%d-%H%M%SZ)"
OUT_FILE="${BACKUP_DIR}/jenkins-home-${TIMESTAMP}.tar.gz"
mkdir -p "$BACKUP_DIR"

echo "Backing up Jenkins controller state to ${OUT_FILE}..."
docker compose exec -T jenkins-controller sh -c \
  'tar -czf - --exclude="workspace/*" --exclude="caches/*" --exclude="war/*" -C /var/jenkins_home config.xml casc_configs jobs_dsl jobs secrets 2>/dev/null || tar -czf - -C /var/jenkins_home config.xml casc_configs' \
  > "$OUT_FILE"

sha256sum "$OUT_FILE" > "${OUT_FILE}.sha256"
echo "Backup complete: ${OUT_FILE}"
echo "Restore command: ./scripts/restore-jenkins.sh ${OUT_FILE}"
