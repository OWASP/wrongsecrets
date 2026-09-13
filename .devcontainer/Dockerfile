FROM eclipse-temurin:26-jdk-resolute

LABEL org.opencontainers.image.title="WrongSecrets Dev Container"
LABEL org.opencontainers.image.description="Development environment for OWASP WrongSecrets"
LABEL org.opencontainers.image.source="https://github.com/OWASP/wrongsecrets"

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

ENV DEBIAN_FRONTEND=noninteractive

# ---------------------------------------------------------------------------
# Base development tooling
# ---------------------------------------------------------------------------

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        git \
        gnupg \
        jq \
        make \
        openssh-client \
        python3 \
        python3-pip \
        unzip \
        wget \
        zip \
    && rm -rf /var/lib/apt/lists/*

# ---------------------------------------------------------------------------
# Node.js
# ---------------------------------------------------------------------------

RUN curl -fsSL https://deb.nodesource.com/setup_26.x | bash - \
    && apt-get update \
    && apt-get install -y --no-install-recommends nodejs \
    && node --version \
    && npm --version \
    && rm -rf /var/lib/apt/lists/*

# ---------------------------------------------------------------------------
# Go
# ---------------------------------------------------------------------------

ARG GO_VERSION=1.27.1

RUN curl -fsSL "https://go.dev/dl/go${GO_VERSION}.linux-$(dpkg --print-architecture).tar.gz" \
        -o /tmp/go.tar.gz \
    && rm -rf /usr/local/go \
    && tar -C /usr/local -xzf /tmp/go.tar.gz \
    && rm /tmp/go.tar.gz

ENV GOPATH="/go"
ENV PATH="/usr/local/go/bin:/go/bin:${PATH}"

RUN mkdir -p /go \
    && go version \
    && go env GOPATH
RUN go install github.com/golangci/golangci-lint/v2/cmd/golangci-lint@v2.13.2
RUN go install github.com/securego/gosec/v2/cmd/gosec@latest

# ---------------------------------------------------------------------------
# Terraform
# ---------------------------------------------------------------------------

RUN install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://apt.releases.hashicorp.com/gpg \
        | gpg --dearmor -o /etc/apt/keyrings/hashicorp-archive-keyring.gpg \
    && chmod a+r /etc/apt/keyrings/hashicorp-archive-keyring.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(. /etc/os-release && echo "$VERSION_CODENAME") main" \
        > /etc/apt/sources.list.d/hashicorp.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends terraform \
    && terraform version \
    && rm -rf /var/lib/apt/lists/*

# ---------------------------------------------------------------------------
# Docker CLI + Compose
# ---------------------------------------------------------------------------

RUN install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
        | gpg --dearmor -o /etc/apt/keyrings/docker.gpg \
    && chmod a+r /etc/apt/keyrings/docker.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
        > /etc/apt/sources.list.d/docker.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        docker-ce-cli \
        docker-compose-plugin \
    && docker --version \
    && docker compose version \
    && rm -rf /var/lib/apt/lists/*

# ---------------------------------------------------------------------------
# Workspace
# ---------------------------------------------------------------------------

WORKDIR /workspace
