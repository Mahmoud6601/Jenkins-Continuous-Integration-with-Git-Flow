"""Unit tests for the minimal DevOps verification payload."""

from __future__ import annotations

import json
import os
import threading
import unittest
from urllib.error import HTTPError
from urllib.request import urlopen

from payload.app import create_server


class ProbePayloadTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.server = create_server("127.0.0.1", 0)
        cls.port = cls.server.server_address[1]
        cls.thread = threading.Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()

    @classmethod
    def tearDownClass(cls) -> None:
        cls.server.shutdown()
        cls.server.server_close()

    def _get(self, path: str) -> tuple[int, dict[str, str], str]:
        url = f"http://127.0.0.1:{self.port}{path}"
        try:
            with urlopen(url, timeout=5) as resp:
                return resp.status, dict(resp.headers), resp.read().decode("utf-8")
        except HTTPError as exc:
            return exc.code, dict(exc.headers), exc.read().decode("utf-8")

    def test_healthz_returns_ok_with_security_headers(self) -> None:
        status, headers, body = self._get("/healthz")
        self.assertEqual(status, 200)
        self.assertEqual(json.loads(body), {"status": "ok"})
        self.assertEqual(headers.get("X-Content-Type-Options"), "nosniff")
        self.assertEqual(headers.get("X-Frame-Options"), "DENY")

    def test_readyz_respects_readiness_flag(self) -> None:
        os.environ["SIMULATE_UNREADY"] = "false"
        status, _, body = self._get("/readyz")
        self.assertEqual(status, 200)
        self.assertEqual(json.loads(body)["status"], "ready")

        os.environ["SIMULATE_UNREADY"] = "true"
        try:
            status, _, body = self._get("/readyz")
            self.assertEqual(status, 503)
            self.assertEqual(json.loads(body)["status"], "not_ready")
        finally:
            os.environ["SIMULATE_UNREADY"] = "false"

    def test_version_propagates_build_metadata(self) -> None:
        os.environ["APP_VERSION"] = "v1.2.3"
        os.environ["GIT_SHA"] = "abc1234"
        os.environ["ENVIRONMENT"] = "staging"
        status, _, body = self._get("/version")
        self.assertEqual(status, 200)
        data = json.loads(body)
        self.assertEqual(data["app_version"], "v1.2.3")
        self.assertEqual(data["git_sha"], "abc1234")
        self.assertEqual(data["environment"], "staging")

    def test_metrics_exposes_prometheus_format(self) -> None:
        status, _, body = self._get("/metrics")
        self.assertEqual(status, 200)
        self.assertIn("probe_requests_total", body)
        self.assertIn("probe_uptime_seconds", body)

    def test_unknown_route_returns_404(self) -> None:
        status, _, body = self._get("/does-not-exist")
        self.assertEqual(status, 404)
        self.assertEqual(json.loads(body)["error"], "not_found")


if __name__ == "__main__":
    unittest.main()
