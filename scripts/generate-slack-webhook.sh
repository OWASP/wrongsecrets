#!/bin/bash

# Script to generate obfuscated Slack webhook URLs for Challenge 59
# Usage: ./generate-slack-webhook.sh [webhook-url]
# If no webhook URL is provided, generates a realistic example

set -e

# Default webhook URL if none provided
DEFAULT_WEBHOOK="https://hooks.slack.com/services/T123456789/B123456789/1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p"

# Function to validate webhook URL format
validate_webhook_url() {
    local url="$1"
    if [[ ! "$url" =~ ^https://hooks\.slack\.com/services/T[A-Z0-9]+/B[A-Z0-9]+/[A-Za-z0-9]+ ]]; then
        echo "Error: Invalid Slack webhook URL format" >&2
        echo "Expected format: https://hooks.slack.com/services/T123456789/B123456789/abcdef123..." >&2
        exit 1
    fi
}

# Function to obfuscate webhook URL with double base64 encoding
obfuscate_webhook() {
    local webhook_url="$1"
    # First base64 encoding (no line wrapping)
    local first_encode=$(echo -n "$webhook_url" | base64 -w 0)
    # Second base64 encoding (no line wrapping)
    local double_encode=$(echo -n "$first_encode" | base64 -w 0)
    echo "$double_encode"
}

# Function to deobfuscate webhook URL (for verification)
deobfuscate_webhook() {
    local obfuscated="$1"
    # First base64 decode
    local first_decode=$(echo -n "$obfuscated" | base64 -d)
    # Second base64 decode
    local original=$(echo -n "$first_decode" | base64 -d)
    echo "$original"
}

# Main script logic
main() {
    echo "=== Slack Webhook URL Generator for Challenge 59 ==="
    echo
    
    # Use provided webhook URL or default
    local webhook_url="${1:-$DEFAULT_WEBHOOK}"
    
    # Validate the webhook URL format
    validate_webhook_url "$webhook_url"
    
    echo "Original webhook URL: $webhook_url"
    echo
    
    # Obfuscate the webhook URL
    local obfuscated=$(obfuscate_webhook "$webhook_url")
    echo "Obfuscated webhook URL (double base64 encoded):"
    echo "$obfuscated"
    echo
    
    # Verification - deobfuscate to ensure it works
    local verified=$(deobfuscate_webhook "$obfuscated")
    echo "Verification (deobfuscated): $verified"
    echo
    
    if [ "$webhook_url" = "$verified" ]; then
        echo "✅ Obfuscation/deobfuscation successful!"
        echo
        echo "To use this in Challenge 59:"
        echo "1. Update application.properties:"
        echo "   CHALLENGE59_SLACK_WEBHOOK_URL=$obfuscated"
        echo
        echo "2. The challenge answer will be:"
        echo "   $webhook_url"
    else
        echo "❌ Error: Obfuscation/deobfuscation failed!"
        exit 1
    fi
}

# Show usage if help is requested
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    echo "Usage: $0 [webhook-url]"
    echo
    echo "Generates double base64 encoded Slack webhook URLs for Challenge 59."
    echo
    echo "Arguments:"
    echo "  webhook-url    Slack webhook URL to obfuscate (optional)"
    echo "                 If not provided, uses a realistic example"
    echo
    echo "Examples:"
    echo "  $0"
    echo "  $0 'https://hooks.slack.com/services/TXXXXXXXX/BXXXXXXXX/abcdef123456'"
    echo
    echo "The webhook URL should follow Slack's format:"
    echo "  https://hooks.slack.com/services/T[TEAM_ID]/B[CHANNEL_ID]/[TOKEN]"
    exit 0
fi

# Run main function
main "$@"