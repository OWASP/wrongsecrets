#!/usr/bin/env bash
set -e

chmod +x ./mvnw

./mvnw dependency:resolve -DskipTests

npm install

pre-commit install
pre-commit install --hook-type commit-msg
