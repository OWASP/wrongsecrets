# Secret Scanner Comparison Workflow

## Overview

The `scanner-comparison.yml` workflow provides a comprehensive benchmark comparing multiple secret scanning tools on the OWASP WrongSecrets repository. This helps understand the relative effectiveness of different secret detection tools.

## Supported Scanners

The workflow currently tests 7 different secret scanning tools:

1. **TruffleHog** - Docker-based secret scanner with verified results
2. **git-secrets** - AWS Labs' git hook for preventing secrets in commits
3. **gitleaks** - High-performance secret scanner with JSON output
4. **detect-secrets** - Yelp's enterprise secret scanner with baseline functionality
5. **gittyleaks** - Python-based secret scanner with broad pattern detection
6. **whispers** - Skyscanner's structured secret detection tool
7. **trufflehog3** - Python version of TruffleHog with additional features

## Running the Workflow

### Manual Execution
```bash
# Trigger manually via GitHub Actions UI
# Go to Actions > Secret Scanner Comparison Benchmark > Run workflow
```

### Scheduled Execution
The workflow automatically runs every Sunday at 02:00 UTC to provide regular benchmarking.

## Output Format

The workflow generates a summary report showing:

```
| Scanner | Secrets Found |
|---------|---------------|
| TruffleHog | X |
| git-secrets | X |
| gitleaks | X |
| detect-secrets | X |
| gittyleaks | X |
| whispers | X |
| trufflehog3 | X |
```

## Expected Results

Based on manual testing, the tools typically find:
- **detect-secrets**: ~98 potential secrets
- **gitleaks**: ~106 secrets
- **gittyleaks**: ~137 findings

Results may vary as the repository evolves and tools update their detection patterns.

## Error Handling

The workflow includes robust error handling:
- Tools that fail to install will show 0 results
- Network timeouts are handled gracefully
- Each scanner job runs independently in parallel
- Failed scans don't block the summary report

## Tool Installation Notes

- **git-secrets**: Compiled from source (no official GitHub Action)
- **gitleaks**: Downloaded as binary from GitHub releases
- **Python tools**: Installed via pip with timeout handling
- **TruffleHog**: Uses official Docker image

## Interpreting Results

Different tools have varying approaches:
- Some focus on verified/confirmed secrets
- Others detect potential patterns that may be false positives
- Count differences are expected and help understand tool characteristics

This benchmark helps users choose appropriate tools for their security scanning needs based on detection coverage and accuracy requirements.
