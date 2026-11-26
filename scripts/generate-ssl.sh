#!/usr/bin/env bash
# Generate local self-signed TLS certificates for Nginx reverse proxy.
set -euo pipefail
cd "$(dirname "$0")/.."

HOST="${1:-localhost}"
mkdir -p nginx/ssl

openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/ssl/nginx-selfsigned.key \
  -out nginx/ssl/nginx-selfsigned.crt \
  -subj "/CN=${HOST}" >/dev/null 2>&1

chmod 600 nginx/ssl/nginx-selfsigned.key
echo "Generated local TLS certificate for '${HOST}' in nginx/ssl/."
