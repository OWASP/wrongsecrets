#!/bin/bash
# Script to extract version from pom.xml and build Docker images with correct version

set -e

# Extract version from pom.xml
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Detected version from pom.xml: $VERSION"

# Remove -SNAPSHOT suffix for Docker tags if present
DOCKER_VERSION=${VERSION%-SNAPSHOT}
echo "Docker version will be: $DOCKER_VERSION"

# Build the application first
echo "Building application..."
./mvnw clean package -DskipTests

# Build main Docker image
echo "Building main Docker image..."
docker build --build-arg argBasedVersion="$DOCKER_VERSION" -t "wrongsecrets:$DOCKER_VERSION" .

# Build web Docker image
echo "Building web Docker image..."
docker build --build-arg argBasedVersion="$DOCKER_VERSION-no-vault" -f Dockerfile.web -t "wrongsecrets:$DOCKER_VERSION-no-vault" .

echo "Build completed successfully!"
echo "Images built:"
echo "  - wrongsecrets:$DOCKER_VERSION"
echo "  - wrongsecrets:$DOCKER_VERSION-no-vault"
