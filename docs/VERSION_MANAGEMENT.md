# Version Management Guide

This document explains how version synchronization works across the WrongSecrets project.

## Overview

The project maintains version consistency between:
- `pom.xml` (Maven project version)
- `Dockerfile` (Docker build argument)
- `Dockerfile.web` (Docker build argument and base image)

## Version Schema

```
pom.xml version:        1.12.4-SNAPSHOT
Dockerfile version:     1.12.4
Dockerfile.web version: 1.12.4-no-vault
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

## Common Debugging Commands

### Version Verification

```bash
# Check current Maven version
mvn help:evaluate -Dexpression=project.version -q -DforceStdout

# Check versions in Dockerfiles
grep "argBasedVersion" Dockerfile Dockerfile.web

# Compare all version references
echo "Maven version: $(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"
echo "Dockerfile build args:"
grep -n "argBasedVersion" Dockerfile*
```

### Build Testing

```bash
# Test Maven build
./mvnw clean compile -q

# Test Docker build with correct version
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
DOCKER_VERSION=${VERSION%-SNAPSHOT}
echo "Building with version: $DOCKER_VERSION"
docker build --build-arg argBasedVersion="$DOCKER_VERSION" -t wrongsecrets:test .

# Verify JAR file exists with correct name
ls -la target/wrongsecrets-*.jar
```

### Troubleshooting Version Mismatches

```bash
# Find all version references in the project
find . -type f \( -name "*.xml" -o -name "Dockerfile*" -o -name "*.yml" -o -name "*.yaml" \) \
  -not -path "./.git/*" -not -path "./target/*" \
  -exec grep -l "1\.[0-9][0-9]\.[0-9]" {} \;

# Check for hard-coded versions in workflows
grep -r "wrongsecrets:" .github/workflows/

# Validate build arguments are being used
docker history wrongsecrets:latest | grep VERSION
```

### CI/CD Debugging

```bash
# Simulate CI version extraction
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "VERSION=${VERSION%-SNAPSHOT}"
echo "Full version: $VERSION"
echo "Docker version: ${VERSION%-SNAPSHOT}"

# Test Docker build as CI would do it
docker build \
  --build-arg argBasedVersion="${VERSION%-SNAPSHOT}" \
  --build-arg argBasedVersionFull="$VERSION" \
  -t wrongsecrets:ci-test .

# Verify JAR is accessible in container
docker run --rm wrongsecrets:ci-test ls -la /tmp/wrongsecrets-*.jar
```

### Quick Health Check

```bash
# Run this script to verify everything is synchronized
./scripts/check-version-sync.sh 2>/dev/null || {
  echo "Version sync check script not found, running manual check:"
  MAVEN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  echo "Maven version: $MAVEN_VERSION"
  echo "Checking Dockerfiles for hard-coded versions..."
  grep -n "[0-9]\+\.[0-9]\+\.[0-9]\+" Dockerfile* | grep -v argBasedVersion || echo "No hard-coded versions found ✓"
}
```

## Best Practices

1. **Always use scripts** for version updates
2. **Never hard-code versions** in CI workflows
3. **Run validation** before committing changes
4. **Update pom.xml first**, then sync other files
5. **Test builds locally** before pushing

This system ensures version consistency and eliminates manual synchronization errors!
