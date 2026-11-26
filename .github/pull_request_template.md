## Git Flow PR Checklist

- **Source branch:** `feature/*` | `bugfix/*` | `release/v*` | `hotfix/v*` | `develop`
- **Target branch:** `develop` | `main`

### Verification
- [ ] Branch name complies with `docs/GITFLOW-POLICY.md`
- [ ] No secrets, `.env`, keys, or credentials included
- [ ] Pipeline / Shared Library unit tests pass
- [ ] Rollback impact assessed (`scripts/rollback.sh` verified)
