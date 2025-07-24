# PR Preview System Setup

This document explains how to set up preview deployments for pull requests in the WrongSecrets project.

## Available Preview Methods

### 1. Full Preview Deployment (Recommended)
- **File**: `.github/workflows/pr-preview.yml`
- **What it does**: Deploys each PR to a temporary environment
- **Requirements**: Render.com account and API key
- **Benefits**: Full functional testing, shareable links

### 2. Build-Only Preview
- **File**: `.github/workflows/build-preview.yml`
- **What it does**: Builds Docker image, provides local testing instructions
- **Requirements**: None (uses GitHub Actions only)
- **Benefits**: No external dependencies, quick setup

### 3. Visual Diff
- **File**: `.github/workflows/visual-diff.yml`
- **What it does**: Takes screenshots comparing PR vs main branch
- **Requirements**: None (uses GitHub Actions only)
- **Benefits**: Visual comparison of UI changes

## Setup Instructions

### For Full Preview Deployment

1. **Create Render.com account** (free tier available)
2. **Get API key** from Render dashboard
3. **Add to GitHub Secrets**:
   - Go to repository Settings > Secrets and variables > Actions
   - Add secret: `RENDER_API_KEY` with your Render API key

4. **Enable the workflow**: The workflow will automatically trigger on PRs

### For Build-Only Preview

1. **No setup required** - workflow uses only GitHub Actions
2. **Enable the workflow**: Already configured to run on template changes

### For Visual Diff

1. **No setup required** - workflow uses only GitHub Actions
2. **Enable the workflow**: Runs automatically when templates change

## How It Works

### PR Lifecycle

1. **PR Opened**: Preview environment is created
2. **New Commits**: Preview is updated automatically
3. **PR Closed**: Preview environment is cleaned up

### What Gets Previewed

- Template changes (`src/main/resources/templates/**`)
- Static content (`src/main/resources/static/**`)
- Challenge explanations (`src/main/resources/explanations/**`)
- Application logic changes

### Generated Comments

Each method adds helpful comments to PRs:
- 🚀 Preview deployment links
- 🔨 Build artifacts and local testing instructions
- 📸 Visual diff artifacts

## Best Practices

1. **Use preview links** in PR descriptions
2. **Test all major user flows** in preview environments
3. **Check visual diffs** for unintended UI changes
4. **Clean up** preview environments when PRs are merged

## Troubleshooting

### Preview Deployment Fails
- Check Render API key is valid
- Verify Docker image builds successfully
- Check for resource limits on free tier

### Visual Diff Missing
- Ensure Playwright can access both versions
- Check for port conflicts
- Verify screenshot paths exist

### Build Artifacts Empty
- Check Maven build succeeds
- Verify Docker build completes
- Look for disk space issues

## Cost Considerations

- **Render.com**: Free tier includes 500 hours/month
- **GitHub Actions**: 2000 minutes/month on free tier
- **Storage**: Minimal for artifacts and images

## Security Notes

- Preview environments use the same Docker image as production
- API keys are stored securely in GitHub Secrets
- Preview URLs are temporary and auto-expire
- No production data is used in previews
