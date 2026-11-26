# Git Flow Branching & SemVer Policy

| Branch | Created From | Merges Into | Image Tags Produced | Deployment Behavior |
| --- | --- | --- | --- | --- |
| `feature/<slug>` | `develop` | `develop` (via PR) | `sha-<short>` (local scan only) | No deploy |
| `bugfix/<slug>` | `develop` | `develop` (via PR) | `sha-<short>` (local scan only) | No deploy |
| `develop` | permanent | `release/vX.Y.Z` | `sha-<short>`, `dev-<build>` | Auto-deploy to `staging` |
| `release/vX.Y.Z` | `develop` | `main` & `develop` | `sha-<short>`, `vX.Y.Z-rc.<build>` | Auto-deploy to `staging` |
| `hotfix/vX.Y.Z` | `main` | `main` & `develop` | `sha-<short>`, `vX.Y.Z-hotfix.<build>` | Manual approval -> `production` |
| `main` | permanent | — | `sha-<short>`, `prod-<build>` | Manual approval -> `production` |

## Branch Protection Recommendations
- Protect both `main` and `develop`: require Pull Requests, require green GitHub Actions CI + Jenkins status checks, block force-pushes, and disallow direct pushes.
