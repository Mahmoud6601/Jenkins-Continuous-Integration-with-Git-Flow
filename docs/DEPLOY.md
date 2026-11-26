# Production Deployment & Webhook Security

## HTTPS & Webhook Configuration
1. Terminate TLS at Nginx (`nginx/conf.d/default.conf.template`) with a CA-issued certificate (Let's Encrypt or internal PKI)—never expose plain HTTP Jenkins webhooks to the internet.
2. In GitHub Repository Settings → Webhooks:
   - **Payload URL:** `https://<your-jenkins-domain>/github-webhook/`
   - **Content type:** `application/json`
   - **Secret:** Match `GITHUB_WEBHOOK_SECRET` stored in Jenkins Credentials Store (enforces HMAC-SHA256 payload verification).
   - **Events:** Select `Pushes` and `Pull requests`.
3. Never use `docker-compose.override.yml` in production; run strictly with `docker compose -f docker-compose.yml up -d`.
