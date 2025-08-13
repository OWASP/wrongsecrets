#!/bin/bash

# Setup pre-commit hooks for OWASP WrongSecrets contributors
# This script ensures that all pre-commit checks are installed and configured

set -e

echo "🔧 Setting up pre-commit hooks for OWASP WrongSecrets..."

# Check if pre-commit is installed
if ! command -v pre-commit &> /dev/null; then
    echo "📦 Installing pre-commit..."
    python3 -m pip install --user pre-commit
fi

# Install pre-commit hooks
echo "⚡ Installing pre-commit hooks..."
pre-commit install

# Install commit-msg hook for commitlint
echo "📝 Installing commit-msg hook..."
pre-commit install --hook-type commit-msg

echo "✅ Pre-commit setup complete!"
echo ""
echo "📋 Available commands:"
echo "  pre-commit run --all-files    # Run all hooks on all files"
echo "  pre-commit run <hook-name>     # Run specific hook"
echo "  pre-commit autoupdate          # Update hook versions"
echo ""
echo "💡 Pre-commit will now run automatically on every commit!"
echo "   To bypass pre-commit checks (not recommended): git commit --no-verify"