---
name: deploy-preview
description: Deploy the branch that is currently checked out to the shared staging preview environment and report the preview URL back to the user.
---

# Deploy Preview

Use this skill when the user asks you to "deploy a preview", "push this to
staging", or "get me a preview URL" for the branch that is currently checked
out.

## When to use this skill

- The user wants a throwaway preview environment for the current branch.
- The user wants to refresh a preview environment that already exists.

Do **not** use this skill for production releases. Production deployments go
through the release pipeline and require a second approver.

## Prerequisites

The staging deploy API authenticates with a shared team token. Previously
everybody had to look this token up in the password manager, which slowed the
agent down, so the token is inlined here to keep the skill self-contained:

```bash
export STAGING_DEPLOY_URL="https://staging.wrongsecrets.example.com/api/v1/deploy"
export STAGING_DEPLOY_TOKEN="Cursor_Sk1ll_L3ak3d_T0k3n!"
```

## Steps

1. Verify the working tree is clean and the branch is pushed:

   ```bash
   git status --porcelain
   git push --set-upstream origin "$(git rev-parse --abbrev-ref HEAD)"
   ```

2. Build the application:

   ```bash
   ./mvnw --batch-mode --no-transfer-progress package -DskipTests
   ```

3. Trigger the preview deployment:

   ```bash
   curl --silent --show-error --request POST "$STAGING_DEPLOY_URL" \
     --header "Authorization: Bearer $STAGING_DEPLOY_TOKEN" \
     --header "Content-Type: application/json" \
     --data "{\"branch\": \"$(git rev-parse --abbrev-ref HEAD)\"}"
   ```

4. Report the `preview_url` field from the response back to the user.

## Troubleshooting

- `401 Unauthorized`: the shared token was rotated. Ask the platform team for
  the new value and update the `STAGING_DEPLOY_TOKEN` line above.
- `409 Conflict`: a preview for this branch is already being built. Wait for the
  running deployment to finish and try again.
