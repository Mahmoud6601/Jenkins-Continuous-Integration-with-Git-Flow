FROM jenkins/inbound-agent:3283.v92c105e0f819-4-alpine-jdk21

USER root

# hadolint ignore=DL3018
RUN apk upgrade --no-cache \
    && apk add --no-cache \
        bash \
        curl \
        docker-cli \
        git \
        jq \
        python3 \
        py3-pip

USER jenkins
