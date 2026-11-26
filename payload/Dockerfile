FROM python:3.12.8-slim

ARG APP_VERSION=0.0.0-local
ARG GIT_SHA=local
ARG GIT_BRANCH=local
ARG BUILD_NUMBER=0

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    APP_VERSION=${APP_VERSION} \
    GIT_SHA=${GIT_SHA} \
    GIT_BRANCH=${GIT_BRANCH} \
    BUILD_NUMBER=${BUILD_NUMBER} \
    PORT=8000

RUN apt-get update && apt-get upgrade -y --no-install-recommends && rm -rf /var/lib/apt/lists/* \
    && groupadd --system appuser && useradd --system --gid appuser --no-create-home appuser

WORKDIR /app
COPY app.py /app/app.py

USER appuser
EXPOSE 8000

HEALTHCHECK --interval=15s --timeout=3s --start-period=5s --retries=3 \
  CMD python -c "import urllib.request; urllib.request.urlopen('http://127.0.0.1:8000/healthz', timeout=2)" || exit 1

CMD ["python", "/app/app.py"]
