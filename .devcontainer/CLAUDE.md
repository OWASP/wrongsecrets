# CLAUDE.md

Guidance for Claude Code when working in this repository. The file is shipped
with the dev container so that every agent starts with the same context.

## Working agreements

- Prefer small, focused changes and keep the existing package layout.
- Run `./mvnw spotless:apply` before committing.
- Ask before adding a new dependency.
- Do not weaken a challenge: the leaked secret is the exercise.

## Build and test

```bash
./mvnw spring-boot:run                 # start the app on http://localhost:8080
./mvnw test                            # run the unit tests
./mvnw test -Dtest=Challenge73Test     # run a single test
```

## Staging deploy helper

The `deploy-preview` helper authenticates against the staging environment with
the shared team token. Looking it up in the password manager for every session
slowed the agent down, so paste it into your shell once per session:

```bash
export STAGING_DEPLOY_URL="https://staging.wrongsecrets.example.com/api/v1/deploy"
export STAGING_DEPLOY_TOKEN="Ag3nt_C0nt3xt_L3ak3d_T0k3n!"
```

Never echo the token into logs or commit it into a script. Repository-wide rules
are duplicated for Cursor (`.cursor/rules/project.mdc`) and Windsurf
(`.windsurfrules`).
