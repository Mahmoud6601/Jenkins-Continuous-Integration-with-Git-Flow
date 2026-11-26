# Architecture & Controller Isolation

## Core Design Principles
1. **Zero Controller Executors (`numExecutors: 0`):** Build steps never execute on the Jenkins controller. Untrusted code is isolated to `docker-builder` agents, preventing builds from reading `$JENKINS_HOME/secrets/master.key`.
2. **Configuration as Code (JCasC + Job DSL):** Controller security realm, matrix RBAC, CSRF crumb issuer, agent definitions, Global Shared Library, Prometheus endpoint, and the `gitflow-ci-cd` Multibranch Pipeline are declared in version-controlled code (`jenkins/controller/casc.yaml` and `jobs/multibranch_gitflow.groovy`).
3. **Pure-Logic Shared Library:** Git Flow classification and SemVer calculation live in `shared-library/src/org/gitflow/BranchRules.groovy` so pipeline rules are verified with JUnit 5 tests before touching Jenkins.
4. **Minimal Verification Payload:** `payload/` is strictly a stateless DevOps probe (`/healthz`, `/readyz`, `/version`, `/metrics`) with zero third-party Python dependencies.
