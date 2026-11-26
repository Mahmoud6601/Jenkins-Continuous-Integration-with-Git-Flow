#!/usr/bin/env bash
# Restore Jenkins controller state from a checksum-verified backup archive.
set -euo pipefail
cd "$(dirname "$0")/.."

BACKUP_FILE="${1:-}"
if [ -z "$BACKUP_FILE" ] || [ ! -f "$BACKUP_FILE" ]; then
  echo "Usage: $0 <backups/jenkins-home-YYYYMMDD-HHMMSSZ.tar.gz>" >&2
  exit 1
fi

if [ -f "${BACKUP_FILE}.sha256" ]; then
  sha256sum -c "${BACKUP_FILE}.sha256"
fi

if [ "${RESTORE_FORCE:-0}" != "1" ]; then
  read -r -p "Restore '${BACKUP_FILE}' into Jenkins controller volume? This OVERWRITES current config. [y/N] " answer
  case "$answer" in
    y|Y|yes|YES) ;;
    *) echo "Aborted."; exit 1 ;;
  esac
fi

docker compose exec -T jenkins-controller tar -xzf - -C /var/jenkins_home < "$BACKUP_FILE"
docker compose restart jenkins-controller
echo "Jenkins restore complete. Verify health with: docker compose ps"
