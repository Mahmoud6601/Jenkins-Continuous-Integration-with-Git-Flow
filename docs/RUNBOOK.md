# Operations, Backup & Rollback Runbook

## 1. Jenkins Controller Backup & Restore
```bash
# Create checksummed backup before any Jenkins LTS or plugin upgrade
./scripts/backup-jenkins.sh

# Restore from verified backup
./scripts/restore-jenkins.sh backups/jenkins-home-YYYYMMDD-HHMMSSZ.tar.gz
```

## 2. Deployment Rollback (Automatic & Manual)
- **Automatic:** `deployAndVerify.groovy` runs `scripts/smoke-test.sh` immediately after `scripts/deploy-env.sh`. If `/healthz`, `/readyz`, `/metrics`, or `/version` SHA verification fails, `scripts/rollback.sh <env>` runs automatically.
- **Manual:**
  ```bash
  ./scripts/rollback.sh production
  # Or roll back to an explicit immutable SHA tag:
  ./scripts/rollback.sh production sha-a1b2c3d
  ```

## 3. Verifying JCasC Reloads
Never hand-edit jobs or global security settings in the Jenkins UI. Update `jenkins/controller/casc.yaml` or `jobs/multibranch_gitflow.groovy`, open a PR so Meta-CI validates the Compose boot, and rebuild the controller image.
