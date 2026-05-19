# WrongSecrets Challenge Roadmap (Post-62)

This roadmap proposes new challenges that complement existing coverage in WrongSecrets.

Goal:
- Add realistic, high-impact secrets misuse cases that are common in modern SDLCs.
- Keep challenges educational, reproducible, and aligned with current project patterns.
- Implement incrementally and co-create each challenge from design to tests.

## Guiding Principles

- Favor real-world attack paths over contrived puzzles.
- Reuse existing patterns: `FixedAnswerChallenge` for static answers, `Challenge` for dynamic behavior.
- Keep challenge runtime support explicit (Docker first, then K8s/Cloud where relevant).
- Add tests and explanation/hint/reason files with each challenge.
- Avoid duplicate coverage with existing challenges (0-62).

## Proposed Backlog (Candidate Challenges 63-72)

## Phase 1: Quick Wins (High Value, Lower Complexity)

1. Challenge 63: CI Artifact Secret Leak
- Misuse: CI workflow uploads artifacts containing `.env` or test output with credentials.
- Learner path: inspect CI artifacts/logs and extract secret.
- Suggested category: CI/CD.
- Runtime: Docker.
- Why now: very common in GitHub Actions and easy to teach.

2. Challenge 64: Package Manager Token in Config
- Misuse: leaked registry credentials in `.npmrc` / `settings.xml` / similar config.
- Learner path: inspect repo/build config for token-bearing auth stanza.
- Suggested category: CI/CD or Intro.
- Runtime: Docker.
- Why now: widespread and highly actionable for attackers.

3. Challenge 65: Frontend Source Map Exposure
- Misuse: production sourcemap reveals API keys/internal endpoints.
- Learner path: retrieve source map, inspect reconstructed source, find secret.
- Suggested category: Front-end.
- Runtime: Docker.
- Why now: complements existing frontend challenges with a distinct failure mode.

4. Challenge 66: OAuth Refresh Token Stored Insecurely
- Misuse: long-lived refresh token persisted in browser storage.
- Learner path: inspect storage/exported session artifacts and recover token.
- Suggested category: Front-end/Auth.
- Runtime: Docker.
- Why now: common in SPA implementations.

## Phase 2: Cloud and Platform Depth

5. Challenge 67: Over-Permissive Signed URL/SAS Token
- Misuse: leaked signed URL with broad scope and long TTL.
- Learner path: find URL in logs/docs/chat mock and use it to retrieve secret object.
- Suggested category: Cloud.
- Runtime: Docker + optional cloud variant.
- Why now: modern cloud-heavy teams often misuse signed links.

6. Challenge 68: Terraform State Leakage
- Misuse: secrets exposed in `tfstate` or plan output.
- Learner path: discover state file or CI plan log and extract secret.
- Suggested category: Terraform.
- Runtime: Docker/Cloud.
- Why now: high prevalence in IaC workflows.

7. Challenge 69: K8s ServiceAccount Token to RBAC Escalation
- Misuse: mounted SA token plus overbroad RBAC allows secret listing.
- Learner path: use in-cluster token to query API and enumerate secrets.
- Suggested category: Kubernetes/Secrets.
- Runtime: K8s.
- Why now: teaches realistic post-compromise movement.

## Phase 3: Advanced Operational Pitfalls

8. Challenge 70: Backup/Snapshot Exposure
- Misuse: backup dump or snapshot in accessible storage contains credentials.
- Learner path: find backup artifact and recover secret from dump.
- Suggested category: Operations.
- Runtime: Docker + optional cloud variant.
- Why now: backup hygiene is a major blind spot.

9. Challenge 71: Observability Pipeline Secret Exfiltration
- Misuse: logs/traces export auth headers/env vars to telemetry backend.
- Learner path: inspect mock telemetry payloads/index and recover secret.
- Suggested category: Logging.
- Runtime: Docker.
- Why now: aligns with modern APM/OTel adoption.

10. Challenge 72: KMS Misuse Despite Encryption At Rest
- Misuse: static data key and broad decrypt permissions undermine KMS controls.
- Learner path: combine exposed config/IAM assumptions to decrypt protected value.
- Suggested category: Cryptography/IAM.
- Runtime: Docker + cloud variant.
- Why now: teaches that "using KMS" alone is not enough.

## Prioritization Matrix

- Highest implementation priority:
  - 63 CI Artifact Secret Leak
  - 64 Package Manager Token in Config
  - 65 Frontend Source Map Exposure
  - 68 Terraform State Leakage
- Highest educational impact for advanced learners:
  - 69 K8s SA Token + RBAC Escalation
  - 71 Observability Exfiltration
  - 72 KMS Misuse

## Suggested Delivery Order

1. 63 CI Artifact Secret Leak
2. 64 Package Manager Token in Config
3. 65 Frontend Source Map Exposure
4. 68 Terraform State Leakage
5. 66 OAuth Refresh Token Stored Insecurely
6. 69 K8s SA Token + RBAC Escalation
7. 67 Over-Permissive Signed URL/SAS Token
8. 70 Backup/Snapshot Exposure
9. 71 Observability Pipeline Exfiltration
10. 72 KMS Misuse

Reasoning:
- Start with Docker-first challenges that are easy to test and review.
- Introduce platform-specific complexity after establishing momentum.
- End with deep cloud/KMS design pitfalls.

## Per-Challenge Implementation Checklist

For each challenge N:

1. Design
- Define exact misuse pattern and attacker path.
- Define expected answer and solvability constraints.
- Decide `FixedAnswerChallenge` vs `Challenge`.
- Select supported runtime environments.

2. Code
- Add challenge class under the appropriate package:
  - `src/main/java/org/owasp/wrongsecrets/challenges/docker/`
  - `src/main/java/org/owasp/wrongsecrets/challenges/kubernetes/`
  - `src/main/java/org/owasp/wrongsecrets/challenges/cloud/`
- Add any support components/controllers if needed.

3. Metadata and Content
- Register challenge in `src/main/resources/wrong-secrets-configuration.yaml`.
- Add explanation files:
  - `src/main/resources/explanations/challengeN.adoc`
  - `src/main/resources/explanations/challengeN_hint.adoc`
  - `src/main/resources/explanations/challengeN_reason.adoc`

4. Tests
- Add unit test mirroring current naming patterns:
  - `src/test/java/.../ChallengeNTest.java`
- Add integration tests only when challenge behavior needs endpoint or environment integration.

5. Validation
- Run focused tests for challenge N.
- Run broader suite if challenge touches shared code paths.
- Ensure CTF behavior and environment gating are correct.

6. Documentation
- Update README challenge availability lists if environment-specific.
- Add setup docs if external services are needed.

## Definition of Done (Per Challenge)

- Challenge implementation merged with tests passing.
- Explanation, hint, and reason files present and review-ready.
- Registered in challenge configuration and visible in UI.
- No regressions in existing challenge behavior.
- Security educational intent clearly stated in explanation/reason.

## Co-Creation Workflow (1 by 1)

For each new challenge we will do:

1. Confirm scope in one short design pass.
2. Implement minimal viable challenge.
3. Add tests and explanation/hint/reason.
4. Validate behavior in the intended environment.
5. Refine difficulty and hints based on review feedback.

## First Candidate To Build Next

Start with Challenge 63 (CI Artifact Secret Leak).

Why:
- High relevance.
- Low infra overhead.
- Fast feedback loop for design and testing.

---

If this roadmap is approved, we can start immediately with Challenge 63 design and implementation in the next iteration.
