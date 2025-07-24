#!/bin/bash
# Script to validate that all version references are in sync

set -e

echo "🔍 Checking version consistency across files..."

# Extract version from pom.xml
POM_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "📄 pom.xml version: $POM_VERSION"

# Extract base version (remove -SNAPSHOT)
BASE_VERSION=${POM_VERSION%-SNAPSHOT}
echo "🐳 Expected Docker base version: $BASE_VERSION"

# Check Dockerfile
DOCKERFILE_VERSION=$(grep "^ARG argBasedVersion=" Dockerfile | cut -d'"' -f2)
echo "🐳 Dockerfile argBasedVersion: $DOCKERFILE_VERSION"

# Check Dockerfile.web
DOCKERFILE_WEB_VERSION=$(grep "^ARG argBasedVersion=" Dockerfile.web | cut -d'"' -f2)
echo "🌐 Dockerfile.web argBasedVersion: $DOCKERFILE_WEB_VERSION"

# Validation
ERRORS=0

if [ "$DOCKERFILE_VERSION" != "$BASE_VERSION" ]; then
    echo "❌ ERROR: Dockerfile version ($DOCKERFILE_VERSION) doesn't match pom.xml base version ($BASE_VERSION)"
    ERRORS=$((ERRORS + 1))
fi

# For Dockerfile.web, check if it follows the pattern BASE_VERSION-no-vault
EXPECTED_WEB_VERSION="$BASE_VERSION-no-vault"
if [ "$DOCKERFILE_WEB_VERSION" != "$EXPECTED_WEB_VERSION" ]; then
    echo "❌ ERROR: Dockerfile.web version ($DOCKERFILE_WEB_VERSION) doesn't match expected pattern ($EXPECTED_WEB_VERSION)"
    ERRORS=$((ERRORS + 1))
fi

# Check base image in Dockerfile.web
BASE_IMAGE_VERSION=$(grep "^FROM jeroenwillemsen/wrongsecrets:" Dockerfile.web | cut -d':' -f2)
echo "🏗️  Dockerfile.web base image version: $BASE_IMAGE_VERSION"

if [ "$BASE_IMAGE_VERSION" != "$EXPECTED_WEB_VERSION" ]; then
    echo "❌ ERROR: Dockerfile.web base image version ($BASE_IMAGE_VERSION) doesn't match argBasedVersion ($DOCKERFILE_WEB_VERSION)"
    ERRORS=$((ERRORS + 1))
fi

# Summary
if [ $ERRORS -eq 0 ]; then
    echo "✅ All versions are in sync!"
    exit 0
else
    echo "❌ Found $ERRORS version mismatches. Please fix them to ensure consistency."
    echo ""
    echo "🔧 To fix automatically, run:"
    echo "   ./scripts/sync-versions.sh"
    exit 1
fi
