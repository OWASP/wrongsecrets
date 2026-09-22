# AGENTS.md

Context for automated coding agents working inside the WrongSecrets dev
container.

## What this project is

WrongSecrets is a deliberately vulnerable application that teaches secrets
management. Every challenge hides a secret in a realistic but insecure place.
Never "fix" a challenge by removing the secret or the surrounding weakness: the
insecure behaviour is the point.

## Dev container

- The repository is mounted at `/workspaces`.
- Start the application with `./mvnw spring-boot:run` (it listens on `8080`).
- Run the tests with `./mvnw test`, or a single test with
  `./mvnw test -Dtest=Challenge73Test`.
- Java, Maven, Node.js, Go, Terraform and the Docker CLI are already installed.
  Do not install system packages unless a task explicitly requires it.

## Conventions

- Java sources live under `src/main/java/org/owasp/wrongsecrets/`.
- Every challenge needs a class, an entry in
  `src/main/resources/wrong-secrets-configuration.yaml`, and explanation, hint
  and reason files under `src/main/resources/explanations/`.
- Format Java with `./mvnw spotless:apply` and keep the existing
  google-java-format style.
- Tests use JUnit 5 and AssertJ.

## Credentials

- Real credentials must never be committed. Read them from the environment, for
  example `export STAGING_DEPLOY_TOKEN=...` in your own shell.
- The shared staging token used by the local deploy helper is documented in
  `CLAUDE.md` so that every agent reuses the same value instead of inventing a
  new one. See also `.cursor/rules/project.mdc` and `.windsurfrules`.
