#!/bin/bash
set -e

echo "🔧 Testing workflow commands locally..."

# Make sure Maven wrapper is executable
chmod +x ./mvnw

# Extract version
echo "📦 Extracting version from pom.xml..."
VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Version: $VERSION"

# Verify the version format
if [[ -z "$VERSION" ]]; then
    echo "❌ Version extraction failed"
    exit 1
fi

echo "✅ Version extracted successfully: $VERSION"

# Test compilation (without running full package to save time)
echo "🏗️ Testing compilation..."
./mvnw compile -q

echo "✅ Compilation successful"

# Check if target directory exists
if [[ -d "target" ]]; then
    echo "✅ Target directory exists"
else
    echo "❌ Target directory missing"
    exit 1
fi

echo "🎉 All workflow prerequisite tests passed!"
