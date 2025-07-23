# Version Management Guide

This document explains how version synchronization works across the WrongSecrets project.

## Overview

The project maintains version consistency between:
- `pom.xml` (Maven project version)
- `Dockerfile` (Docker build argument)
- `Dockerfile.web` (Docker build argument and base image)

## Version Schema

```
pom.xml version:        1.12.3B2-SNAPSHOT
Dockerfile version:     1.12.3B2
Dockerfile.web version: 1.12.3B2-no-vault
```

## Automated Solutions

### 1. GitHub Actions Integration

All build workflows now automatically extract the version from `pom.xml`:

```yaml
- name: Extract version from pom.xml
  id: extract-version
  run: |
    VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
    DOCKER_VERSION=${VERSION%-SNAPSHOT}
    echo "docker_version=$DOCKER_VERSION" >> $GITHUB_OUTPUT

- name: Build Docker image
  run: |
    docker build --build-arg argBasedVersion="${{ steps.extract-version.outputs.docker_version }}" -t image .
```

### 2. Version Sync Scripts

#### Validate Versions
```bash
./scripts/validate-versions.sh
```
Checks if all versions are consistent and reports mismatches.

#### Auto-Sync Versions
```bash
./scripts/sync-versions.sh
```
Automatically updates Dockerfiles to match `pom.xml` version.

#### Build with Version Sync
```bash
./scripts/build-with-version-sync.sh
```
Builds both Docker images with correct versions from `pom.xml`.

### 3. CI/CD Integration

The `version-sync-check.yml` workflow:
- ✅ Runs on PR/push when version files change
- ✅ Validates version consistency
- ✅ Comments on PRs with fix instructions if mismatched
- ✅ Prevents version drift

## Manual Process

### When Updating Versions

1. **Update pom.xml version**:
   ```xml
   <version>1.13.0-SNAPSHOT</version>
   ```

2. **Run sync script**:
   ```bash
   ./scripts/sync-versions.sh
   ```

3. **Verify changes**:
   ```bash
   ./scripts/validate-versions.sh
   ```

4. **Commit all changes**:
   ```bash
   git add pom.xml Dockerfile Dockerfile.web
   git commit -m "Bump version to 1.13.0"
   ```

## Workflow Integration

### All Build Workflows Include:

1. **Version Extraction**: Gets version from `pom.xml`
2. **Dynamic Build Args**: Passes version to Docker build
3. **Validation**: Ensures JAR file matches expected name
4. **Logging**: Shows which versions are being used

### Benefits:

- ✅ **Single Source of Truth**: `pom.xml` is the authoritative version
- ✅ **No Manual Updates**: Dockerfiles auto-sync with Maven version
- ✅ **CI Validation**: Catches version mismatches early
- ✅ **Consistent Builds**: Same version used across all environments

## Troubleshooting

### Common Issues:

1. **JAR Not Found**: Version mismatch between build arg and actual JAR name
   - **Solution**: Run `./scripts/sync-versions.sh`

2. **Docker Build Fails**: Hard-coded version in Dockerfile
   - **Solution**: Use `--build-arg argBasedVersion=...`

3. **CI Version Mismatch**: Manual updates to Dockerfiles
   - **Solution**: Let CI extract from `pom.xml` dynamically

### Debug Commands:

```bash
# Check current versions
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
grep "argBasedVersion" Dockerfile Dockerfile.web

# Test build with current version
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
DOCKER_VERSION=${VERSION%-SNAPSHOT}
docker build --build-arg argBasedVersion="$DOCKER_VERSION" .
```

## Best Practices

1. **Always use scripts** for version updates
2. **Never hard-code versions** in CI workflows
3. **Run validation** before committing changes
4. **Update pom.xml first**, then sync other files
5. **Test builds locally** before pushing

This system ensures version consistency and eliminates manual synchronization errors!
