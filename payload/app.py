"""Minimal stateless DevOps verification payload.

Exposes only operational endpoints (/healthz, /readyz, /version, /metrics) so the
Jenkins Git Flow pipeline can prove container builds, security scans, staged rollouts,
smoke tests, and instant rollbacks without unnecessary business logic.
"""

from __future__ import annotations

import json
import os
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from typing import Any

START_TIME = time.monotonic()
REQUEST_COUNT = 0


def get_version_payload() -> dict[str, str]:
    """Return deployment metadata injected at container build/runtime."""
    return {
        "service": "gitflow-probe-payload",
        "app_version": os.getenv("APP_VERSION", "0.0.0-local"),
        "git_sha": os.getenv("GIT_SHA", "local"),
        "branch": os.getenv("GIT_BRANCH", "local"),
        "environment": os.getenv("ENVIRONMENT", "local"),
        "build_number": os.getenv("BUILD_NUMBER", "0"),
    }


def render_metrics() -> str:
    """Render minimal Prometheus text exposition metrics."""
    uptime = max(0.0, time.monotonic() - START_TIME)
    env = os.getenv("ENVIRONMENT", "local")
    return (
        "# HELP probe_requests_total Total HTTP requests served by the verification payload.\n"
        "# TYPE probe_requests_total counter\n"
        f'probe_requests_total{{environment="{env}"}} {REQUEST_COUNT}\n'
        "# HELP probe_uptime_seconds Process uptime in seconds.\n"
        "# TYPE probe_uptime_seconds gauge\n"
        f'probe_uptime_seconds{{environment="{env}"}} {uptime:.2f}\n'
    )


class ProbeHandler(BaseHTTPRequestHandler):
    """HTTP handler serving operational health, version, and metrics probes."""

    server_version = "probe"
    sys_version = ""

    def _send_json(self, status: int, body: dict[str, Any]) -> None:
        raw = json.dumps(body).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(raw)))
        self.send_header("X-Content-Type-Options", "nosniff")
        self.send_header("X-Frame-Options", "DENY")
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(raw)

    def _send_text(self, status: int, text: str, content_type: str) -> None:
        raw = text.encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(raw)))
        self.send_header("X-Content-Type-Options", "nosniff")
        self.end_headers()
        self.wfile.write(raw)

    def do_GET(self) -> None:  # noqa: N802
        global REQUEST_COUNT
        REQUEST_COUNT += 1
        path = self.path.split("?", 1)[0]

        if path == "/healthz":
            self._send_json(200, {"status": "ok"})
            return
        if path == "/readyz":
            ready = os.getenv("SIMULATE_UNREADY", "false").lower() != "true"
            self._send_json(200 if ready else 503, {"status": "ready" if ready else "not_ready"})
            return
        if path in ("/version", "/"):
            self._send_json(200, get_version_payload())
            return
        if path == "/metrics":
            self._send_text(200, render_metrics(), "text/plain; version=0.0.4; charset=utf-8")
            return

        self._send_json(404, {"error": "not_found"})

    def log_message(self, fmt: str, *args: Any) -> None:  # pragma: no cover
        return


def create_server(host: str = "0.0.0.0", port: int = 8000) -> ThreadingHTTPServer:
    return ThreadingHTTPServer((host, port), ProbeHandler)


if __name__ == "__main__":  # pragma: no cover
    port = int(os.getenv("PORT", "8000"))
    server = create_server("0.0.0.0", port)
    server.serve_forever()
