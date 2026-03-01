# Spring Boot 4 Adoption Checklist (WrongSecrets)

This checklist is tailored to the current `wrongsecrets` codebase (Spring Boot `4.0.3`, Java `25`).

## How to use this document

- Keep this as a living checklist in PRs.
- Mark items complete when merged.
- Prefer small, focused migrations (one concern per PR).

## Current baseline (already in place)

- [x] Spring Boot `4.0.3` is configured in `pom.xml`.
- [x] Spring Cloud line is aligned (`2025.1.1`).
- [x] `@ConfigurationProperties` is already used in multiple places.
- [x] Mockito inline-mock-maker warning addressed by passing Mockito as Java agent in Surefire.

---

## Priority 0 — Safety and consistency (start here)

### 1) Standardize HTTP error responses with `ProblemDetail`

- [ ] Add a global `@RestControllerAdvice` for API endpoints that returns `ProblemDetail`.
- [ ] Keep MVC HTML error handling as-is for Thymeleaf pages; only modernize JSON API errors.
- [ ] Add tests that assert RFC 9457-style payload fields (`type`, `title`, `status`, `detail`, `instance`).

**Why now:** Reduces custom exception payload drift and improves API consistency.

### 2) Replace new `RestTemplate` usage with `RestClient`

- [ ] Stop introducing any new `RestTemplate` usage.
- [ ] Migrate existing bean in `WrongSecretsApplication` from `RestTemplate` to `RestClient.Builder`.
- [ ] Migrate call sites incrementally (start with `SlackNotificationService`).
- [ ] Add timeout and retry policy explicitly for outbound calls.

**Current state:** `RestTemplate` bean and usage exist and can be migrated safely in phases.

### 3) Add/verify deprecation gate in CI

- [ ] Run compile with deprecation warnings enabled in CI (`-Xlint:deprecation`).
- [ ] Fail build on newly introduced deprecations (can be soft-fail initially).
- [ ] Track remaining suppressions/deprecations as explicit TODOs.

**Why now:** Boot 4/Spring 7 deprecations will accumulate quickly otherwise.

---

## Priority 1 — Observability and operability

### 4) Enable tracing + log correlation end-to-end

- [ ] Ensure tracing is enabled in all non-local profiles.
- [ ] Ensure logs include trace/span correlation IDs.
- [ ] Add dashboard/alerts for key challenge-flow operations.

### 5) Harden Actuator for production profiles

- [ ] Verify readiness/liveness probes are exposed and used by deployment manifests.
- [ ] Restrict sensitive actuator endpoints by profile.
- [ ] Add health contributors for external dependencies used in runtime profiles.

### 6) Structured logging profile

- [ ] Use JSON logs for cloud/container profiles.
- [ ] Keep developer-friendly text logs for local profile.
- [ ] Document expected log fields for incident response.

---

## Priority 2 — Runtime and performance

### 7) Evaluate virtual threads for I/O-heavy flows

- [ ] Add profile-based toggle (`spring.threads.virtual.enabled=true`) for evaluation.
- [ ] Run load comparison (latency, throughput, memory) before default-enabling.
- [ ] Keep a rollback toggle in case of third-party incompatibilities.

### 8) Validate graceful shutdown behavior

- [ ] Verify request drain behavior on shutdown in containerized environments.
- [ ] Confirm no challenge state corruption occurs during rolling updates.

### 9) AOT/native readiness checks

- [ ] Add optional CI job for AOT/native compatibility (not necessarily release artifact yet).
- [ ] Record blockers (reflection/dynamic proxies/resources) in this document.

---

## Priority 3 — Security and configuration posture

### 10) Expand typed config, reduce scattered `@Value`

- [ ] Introduce/extend `@ConfigurationProperties` classes for grouped settings.
- [ ] Limit direct `@Value` usage to simple one-off values.
- [ ] Validate config with bean validation annotations.

### 11) TLS/SSL bundles standardization

- [ ] Use SSL bundle config for outbound TLS trust/key material where applicable.
- [ ] Remove ad-hoc SSL setup code if present.

### 12) Secret handling consistency by profile

- [ ] Document expected secret source per profile (`docker`, `k8s`, `aws`, `gcp`, `azure`).
- [ ] Ensure no fallback path accidentally logs sensitive values.

---

## Priority 4 — Testing modernization

### 13) Keep Mockito java-agent setup stable

- [x] Surefire passes Mockito as `-javaagent`.
- [ ] Mirror same setup in Failsafe if/when integration tests use inline mocking.

### 14) Strengthen integration testing with Testcontainers service connection patterns

- [ ] Prefer service-connection style wiring for test dependencies.
- [ ] Reduce custom bootstrapping code in integration tests where possible.

### 15) Add contract tests for outbound HTTP clients

- [ ] Add tests for success, timeout, retry, and non-2xx mapping behavior.
- [ ] Ensure migrated `RestClient` paths are fully covered.

---

## Concrete first 5 PRs

1. **PR 1:** Add API `ProblemDetail` advice + tests.
2. **PR 2:** Introduce `RestClient` bean and migrate `SlackNotificationService`.
3. **PR 3:** Add deprecation checks to CI and document policy.
4. **PR 4:** Add tracing/log-correlation defaults for non-local profiles.
5. **PR 5:** Virtual thread evaluation profile + benchmark notes.

---

## Definition of done for Boot 4 adoption

- [ ] No new `RestTemplate` code introduced.
- [ ] API errors are standardized on `ProblemDetail`.
- [ ] Deprecation warnings are tracked and controlled in CI.
- [ ] Observability baseline (metrics, traces, log correlation) is active in non-local profiles.
- [ ] Migration choices and rollout decisions are documented in `docs/`.
