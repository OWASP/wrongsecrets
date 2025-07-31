#!/bin/bash
# Simple test script to verify the Thymeleaf preview generation works

set -e

echo "🧪 Testing Thymeleaf Preview Generation..."

# Clean up any existing test files
rm -rf test-static-site

# Test the script
python3 .github/scripts/generate_thymeleaf_previews.py 999

# Check that files were generated
if [ ! -d "static-site/pr-999/pages" ]; then
    echo "❌ Error: Pages directory not created"
    exit 1
fi

# Check that all expected files exist
expected_files=("welcome.html" "about.html" "stats.html" "challenge-example.html")
for file in "${expected_files[@]}"; do
    if [ ! -f "static-site/pr-999/pages/$file" ]; then
        echo "❌ Error: $file was not generated"
        exit 1
    fi
    echo "✅ Generated $file"
done

# Check that HTML contains expected content
if ! grep -q "OWASP WrongSecrets" "static-site/pr-999/pages/welcome.html"; then
    echo "❌ Error: welcome.html missing expected content"
    exit 1
fi

if ! grep -q "About WrongSecrets" "static-site/pr-999/pages/about.html"; then
    echo "❌ Error: about.html missing expected content"  
    exit 1
fi

if ! grep -q "Current Stats" "static-site/pr-999/pages/stats.html"; then
    echo "❌ Error: stats.html missing expected content"
    exit 1
fi

# Check file sizes (should not be empty)
for file in "${expected_files[@]}"; do
    size=$(stat -c%s "static-site/pr-999/pages/$file")
    if [ "$size" -lt 1000 ]; then
        echo "❌ Error: $file is too small ($size bytes)"
        exit 1
    fi
    echo "✅ $file has good size ($size bytes)"
done

# Clean up
rm -rf static-site

echo "🎉 All tests passed! Thymeleaf preview generation is working correctly."