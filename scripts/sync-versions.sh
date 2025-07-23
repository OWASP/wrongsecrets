#!/bin/bash
# Script to automatically sync versions across all files

set -e

echo "🔄 Syncing versions across all files..."

# Extract version from pom.xml
POM_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
BASE_VERSION=${POM_VERSION%-SNAPSHOT}
WEB_VERSION="$BASE_VERSION-no-vault"

echo "📄 Source version (pom.xml): $POM_VERSION"
echo "🐳 Target Docker version: $BASE_VERSION"
echo "🌐 Target web version: $WEB_VERSION"

# Update Dockerfile
echo "🔧 Updating Dockerfile..."
sed -i.bak "s/^ARG argBasedVersion=.*/ARG argBasedVersion=\"$BASE_VERSION\"/" Dockerfile
rm -f Dockerfile.bak

# Update Dockerfile.web
echo "🔧 Updating Dockerfile.web..."
sed -i.bak "s/^FROM jeroenwillemsen\/wrongsecrets:.*/FROM jeroenwillemsen\/wrongsecrets:$WEB_VERSION/" Dockerfile.web
sed -i.bak "s/^ARG argBasedVersion=.*/ARG argBasedVersion=\"$WEB_VERSION\"/" Dockerfile.web
rm -f Dockerfile.web.bak

# Verify changes
echo "✅ Version sync completed!"
echo ""
echo "📋 Updated versions:"
echo "   - Dockerfile: $(grep "^ARG argBasedVersion=" Dockerfile | cut -d'"' -f2)"
echo "   - Dockerfile.web: $(grep "^ARG argBasedVersion=" Dockerfile.web | cut -d'"' -f2)"
echo "   - Dockerfile.web base image: $(grep "^FROM jeroenwillemsen/wrongsecrets:" Dockerfile.web | cut -d':' -f2)"
echo ""
echo "🔍 Run './scripts/validate-versions.sh' to verify all versions are in sync."
