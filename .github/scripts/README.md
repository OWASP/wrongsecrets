# Thymeleaf Preview Generation for GitHub Pages

## Overview

This directory contains scripts to generate static HTML previews from Thymeleaf templates for GitHub Pages PR previews.

## Files

- `generate_thymeleaf_previews.py` - Main script that converts Thymeleaf templates to static HTML
- `test_thymeleaf_generation.sh` - Test script to verify the generation works
- `update_pr_index.py` - Updates the main GitHub Pages index with PR information  
- `remove_pr_from_index.py` - Removes PR information when PR is closed

## How It Works

### Thymeleaf Template Processing

The `generate_thymeleaf_previews.py` script:

1. **Parses Thymeleaf Templates**: Reads template files from `src/main/resources/templates/`
2. **Processes Thymeleaf Syntax**: Converts Thymeleaf expressions to static HTML:
   - `th:text="${variable}"` → Static text with mock data
   - `th:each="item : ${items}"` → Generated HTML loops
   - `th:if="${condition}"` → Conditional content
   - `th:href="@{/path}"` → Static links
3. **Adds Mock Data**: Provides realistic preview data for:
   - Challenge information
   - User stats and configuration
   - Session data
   - Canary token information
4. **Generates Navigation**: Adds navigation between preview pages
5. **Outputs Static HTML**: Saves processed templates as standalone HTML files

### Generated Pages

- **welcome.html** - Home page with challenge table (from `welcome.html` template)
- **about.html** - About page with project information (from `about.html` template)  
- **stats.html** - Stats and configuration page (from `stats.html` template)
- **challenge-example.html** - Sample challenge page (from `challenge.html` template)

### Integration with GitHub Actions

The script is integrated into the GitHub Pages preview workflow:

1. PR is opened/updated with template changes
2. GitHub Actions runs the build process
3. Script generates static HTML from updated templates
4. Static files are deployed to GitHub Pages
5. PR comment includes links to preview pages

## Usage

### Manual Testing

```bash
# Generate previews for PR number 123
python3 .github/scripts/generate_thymeleaf_previews.py 123

# Run tests
./.github/scripts/test_thymeleaf_generation.sh
```

### GitHub Actions Integration

The script runs automatically in the `github-pages-preview.yml` workflow when:
- PRs are opened, synchronized, or reopened
- Changes are made to templates or related files

## Mock Data

The script includes realistic mock data:

- **Challenges**: 10 sample challenges with different difficulties and environments
- **Stats**: Session counters, canary callbacks, configuration settings
- **User State**: Some challenges marked as completed for preview purposes
- **Configuration**: Hints, reasons, CTF mode, and other feature flags

## Benefits

- **Better PR Reviews**: Reviewers can see rendered HTML instead of raw templates
- **Visual Validation**: Changes to styling and layout are immediately visible
- **No Build Required**: Works without running the full Spring Boot application
- **Fast Preview**: Generates quickly using mock data
- **Mobile Friendly**: Responsive design works on all devices

## Limitations

- Uses mock data instead of real application data
- Some dynamic features (JavaScript interactions) may not work fully
- Template fragments and includes are simplified
- Complex Thymeleaf expressions may need manual handling

## Adding New Templates

To add preview support for new templates:

1. Add the template processing logic to `ThymeleafToStaticConverter.generate_*_page()` method
2. Add mock data for any new variables in `self.mock_data`
3. Add navigation links in `generate_navigation_html()`
4. Update the main preview page to link to the new page
5. Test with the test script

## Troubleshooting

- **Missing templates**: Script will generate fallback content
- **Parse errors**: Check Thymeleaf syntax in templates
- **Empty output**: Verify mock data contains expected variables
- **Broken layout**: Check CSS/JS file paths in static assets