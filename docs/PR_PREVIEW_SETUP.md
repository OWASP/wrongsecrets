# PR Preview System Setup

This document explains how to set up preview deployments for pull requests in the WrongSecrets project.

## Available Preview Methods

### 1. GitHub Pages Static Preview (NEW)
- **File**: `.github/workflows/github-pages-preview.yml`
- **What it does**: Creates static HTML previews on GitHub Pages for each PR
- **Requirements**: GitHub Pages enabled for the repository
- **Benefits**: Fast, lightweight previews of UI changes, automatic cleanup, no external dependencies
- **URL Format**: `https://owasp.github.io/wrongsecrets/pr-{number}/`
- **Index Page**: `https://owasp.github.io/wrongsecrets/` (lists all active PR previews)

### 2. Full Preview Deployment (Recommended for testing)
- **File**: `.github/workflows/pr-preview.yml`
- **What it does**: Deploys each PR to a temporary environment
- **Requirements**: Render.com account and API key
- **Benefits**: Full functional testing, shareable links

### 3. Build-Only Preview
- **File**: `.github/workflows/build-preview.yml`
- **What it does**: Builds Docker image, provides local testing instructions
- **Requirements**: None (uses GitHub Actions only)
- **Benefits**: No external dependencies, quick setup

### 4. Visual Diff
- **File**: `.github/workflows/visual-diff.yml`
- **What it does**: Takes screenshots comparing PR vs main branch
- **Requirements**: None (uses GitHub Actions only)
- **Benefits**: Visual comparison of UI changes

## Setup Instructions

### For GitHub Pages Static Preview

1. **Enable GitHub Pages** in repository settings:
   - Go to repository Settings > Pages
   - Source: GitHub Actions
   - No additional configuration needed

2. **The workflow is automatic**: Once the workflow file is present, it will:
   - Trigger on PRs affecting templates, static files, or Java code
   - Generate static preview with all CSS, JS, and assets
   - Deploy to GitHub Pages with PR-specific URL
   - **Maintain a shared index** that lists all active PR previews
   - Clean up automatically when PR is closed

3. **Multiple PR Support**: The system now supports multiple PRs simultaneously:
   - Each PR gets its own preview directory: `pr-{number}/`
   - All PRs are listed on the main index page
   - PRs can be deployed and updated in parallel
   - Cleanup removes only the specific PR without affecting others

4. **What gets previewed**:
   - All static assets (CSS, JavaScript, images)
   - Basic HTML structure and styling
   - Theme toggle and UI components
   - Links to full Docker preview for functionality testing

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

### Multiple PR Previews Not Showing
- Check that each PR is creating its own directory structure
- Verify the main index page is being updated correctly
- Look for deployment conflicts in GitHub Actions logs

### Preview Deployment Fails
- Check that GitHub Pages is enabled and set to GitHub Actions source
- Verify Docker image builds successfully
- Check for resource limits or permission issues

### Cleanup Not Working
- Ensure the PR close event is triggering the cleanup job
- Check that the gh-pages branch exists and is accessible
- Verify the Python scripts can parse and update the index file

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
