#!/usr/bin/env python3
"""
Script to generate static HTML previews from Thymeleaf templates.
This creates simplified static versions of the main pages for GitHub Pages preview.
"""

import os
import re
import html
from pathlib import Path


class ThymeleafToStaticConverter:
    """Convert Thymeleaf templates to static HTML with mock data."""

    def __init__(self, templates_dir, static_dir, pr_number):
        self.templates_dir = Path(templates_dir)
        self.static_dir = Path(static_dir)
        self.pr_number = pr_number

        # Mock data for template rendering
        self.mock_data = {
            "totalScore": 42,
            "sessioncounter": 15,
            "canaryCounter": 3,
            "lastCanaryToken": "mock-canary-token-12345",
            "canarytokenURLs": [
                "https://example.canarytokens.org/token1",
                "https://example.canarytokens.org/token2",
            ],
            "hintsEnabled": True,
            "reasonEnabled": True,
            "ctfModeEnabled": False,
            "spoilingEnabled": True,
            "swaggerUIEnabled": True,
            "springdocenabled": True,
            "swaggerURI": "/v3/api-docs",
            "environment": "Docker (Heroku)",
            "challenges": self.generate_mock_challenges(),
            "ctfServerAddress": None,
            "allCompleted": False,
        }

    def generate_mock_challenges(self):
        """Generate mock challenge data."""
        challenges = []
        challenge_names = [
            "Find the hard-coded password",
            "Find the unencrypted password in Git",
            "Find the hard-coded password in front-end",
            "Take a look at this file",
            "Find the AWS S3 bucket password",
            "Find the Azure Key Vault secret",
            "Connect the dots with Docker",
            "Find the secret in the container",
            "Retrieve cloud instance metadata",
            "Use AWS Parameter Store",
            "Find the secret in SSM",
            "Find the Docker secret",
            "GitHub Actions secret",
            "Find the K8s secret",
            "Find the hardcoded secret",
            "Front-end secret exposure",
            "Bash history secret",
            "Find the secret in logs",
            "Find the encrypted secret",
            "Binary analysis secret",
            "Go binary secret",
            "Rust binary secret",
            "Front-end secret part 2",
            "Web3 secret exposure",
            "Smart contract secret",
            "Binary secret 2",
            "Terraform secret",
            "GitHub issue secret",
            "Log analysis secret",
            "Web page secret",
            "AI prompt injection",
            "Container debugging",
            "Password shucking",
            "Random key generation",
            "Vulnerability reporting",
            "Binary without strings",
            "Security test access",
            "Git notes secret",
            "Insecure encryption key",
            "Encryption key storage",
            "Password shucking 2",
            "Audit event secret",
            "Vault template injection",
            "Multi-environment secret",
            "Vault subkey challenge",
            "HashiCorp Vault injection",
            "Binary secret 3",
            "Binary secret 4",
            "AES MD5 cracking",
            "Docker secret 2",
            "Binary secret 5",
            "Docker secret 3",
            "Container debugging 2",
            "Docker secret 4",
            "SSH bastion secret",
            "AI secret exposure",
            "LLM API key exposure in JavaScript",  # Challenge 57
            "Database connection string exposure",  # Challenge 58
        ]

        difficulties = ["⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐", "⭐⭐⭐⭐⭐"]
        techs = [
            "DEVOPS",
            "GIT",
            "FRONTEND",
            "DEVOPS",
            "AWS",
            "AZURE",
            "DOCKER",
            "DOCKER",
            "AWS",
            "AWS",
            "AWS",
            "DOCKER",
            "CI/CD",
            "K8S",
            "DEVOPS",
            "FRONTEND",
            "DEVOPS",
            "LOGGING",
            "CRYPTO",
            "BINARY",
            "BINARY",
            "BINARY",
            "FRONTEND",
            "WEB3",
            "WEB3",
            "BINARY",
            "TERRAFORM",
            "GIT",
            "LOGGING",
            "FRONTEND",
            "AI",
            "DOCKER",
            "PASSWORD",
            "CRYPTO",
            "DOCS",
            "BINARY",
            "CI/CD",
            "GIT",
            "CRYPTO",
            "CRYPTO",
            "PASSWORD",
            "LOGGING",
            "VAULT",
            "VAULT",
            "VAULT",
            "VAULT",
            "BINARY",
            "BINARY",
            "CRYPTO",
            "DOCKER",
            "BINARY",
            "DOCKER",
            "DOCKER",
            "DOCKER",
            "DOCKER",
            "AI",
            "AI",
            "LOGGING",
        ]
        environments = [
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "AWS",
            "Azure",
            "Docker",
            "Docker",
            "AWS",
            "AWS",
            "AWS",
            "Docker",
            "Docker",
            "K8s",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "AWS",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "K8s",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "K8s with Vault",
            "K8s with Vault",
            "K8s with Vault",
            "K8s with Vault",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "K8s",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
            "Docker",
        ]

        for i, name in enumerate(challenge_names):
            challenge = {
                "name": name,
                "link": f"challenge-{i+1}",
                "challengeCompleted": i < 3,  # First 3 completed
                "isChallengeEnabled": True,
                "tech": techs[i % len(techs)],
                "stars": difficulties[i % len(difficulties)],
                "starsOnScale": difficulties[i % len(difficulties)],
                "runtimeEnvironmentCategory": environments[i % len(environments)],
                "dataLabel": f"challenge-{i+1}-name",
            }
            challenges.append(challenge)

        return challenges

    def process_thymeleaf_syntax(self, content, template_name):
        """Convert Thymeleaf syntax to static HTML with mock data."""
        # Replace layout declarations
        content = re.sub(
            r'<html[^>]*layout:decorate="~\{[^}]+\}"[^>]*>', '<html lang="en">', content
        )
        content = re.sub(r'xmlns:layout="[^"]*"', "", content)
        content = re.sub(r'xmlns:th="[^"]*"', "", content)

        # Replace layout fragments
        content = re.sub(
            r'<div[^>]*layout:fragment="content"[^>]*>',
            '<div class="container">',
            content,
        )

        # Replace th:text attributes with mock data
        content = self.replace_th_text(content)

        # Replace th:if conditionals
        content = self.replace_th_if(content)

        # Replace th:each loops
        content = self.replace_th_each(content)

        # Replace th:href attributes
        content = self.replace_th_href(content)

        # Replace th:attr attributes
        content = self.replace_th_attr(content)

        # Remove remaining Thymeleaf attributes
        content = re.sub(r'\s*th:[a-zA-Z-]+="[^"]*"', "", content)

        # Add CSS and JS links for the static preview
        content = self.add_static_assets(content, template_name)

        return content

    def replace_th_text(self, content):
        """Replace th:text attributes with mock values."""
        # Handle th:text patterns with proper content replacement
        patterns = [
            (
                r'<span[^>]*th:text="\$\{totalScore\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["totalScore"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{sessioncounter\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["sessioncounter"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{canaryCounter\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["canaryCounter"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{lastCanaryToken\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["lastCanaryToken"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{hintsEnabled\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["hintsEnabled"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{reasonEnabled\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["reasonEnabled"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{ctfModeEnabled\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["ctfModeEnabled"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{spoilingEnabled\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["spoilingEnabled"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{swaggerUIEnabled\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["swaggerUIEnabled"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{springdocenabled\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["springdocenabled"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{swaggerURI\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["swaggerURI"]}</span>',
            ),
            (
                r'<span[^>]*th:text="\$\{#strings\.replace\(environment,\'_\',\' _\'\)\}"[^>]*>[^<]*</span>',
                f'<span>{self.mock_data["environment"]}</span>',
            ),
            (
                r'<p[^>]*th:text="\'Total score: \'\+\$\{totalScore\}"[^>]*>[^<]*</p>',
                f'<p>Total score: {self.mock_data["totalScore"]}</p>',
            ),
        ]

        for pattern, replacement in patterns:
            content = re.sub(pattern, replacement, content)

        # Also handle simple th:text attributes without full element matching
        simple_replacements = {
            r'th:text="\$\{totalScore\}"': "",
            r'th:text="\$\{sessioncounter\}"': "",
            r'th:text="\$\{canaryCounter\}"': "",
            r'th:text="\$\{lastCanaryToken\}"': "",
            r'th:text="\$\{hintsEnabled\}"': "",
            r'th:text="\$\{reasonEnabled\}"': "",
            r'th:text="\$\{ctfModeEnabled\}"': "",
            r'th:text="\$\{spoilingEnabled\}"': "",
            r'th:text="\$\{swaggerUIEnabled\}"': "",
            r'th:text="\$\{springdocenabled\}"': "",
            r'th:text="\$\{swaggerURI\}"': "",
            r'th:text="\$\{#strings\.replace\(environment,\'_\',\' _\'\)\}"': "",
            r'th:text="\'Total score: \'\+\$\{totalScore\}"': "",
        }

        for pattern, replacement in simple_replacements.items():
            content = re.sub(pattern, replacement, content)

        return content

    def replace_th_if(self, content):
        """Handle th:if conditionals."""
        # For preview purposes, show most content by removing th:if attributes
        # but hide content that would cause issues
        content = re.sub(r'th:if="\$\{ctfServerAddress != null\}"[^>]*>', "", content)
        content = re.sub(r'th:if="\$\{ctfServerAddress == null\}"[^>]*>', "", content)
        content = re.sub(r'th:if="\$\{springdocenabled==true\}"[^>]*>', "", content)
        content = re.sub(r'th:if="\$\{[^}]+\}"', "", content)

        return content

    def replace_th_each(self, content):
        """Replace th:each loops with static content."""
        # Handle challenges loop
        if 'th:each="challenge,iter: ${challenges}"' in content:
            challenge_rows = ""
            for i, challenge in enumerate(self.mock_data["challenges"]):
                solved_class = "solved" if challenge["challengeCompleted"] else ""
                solved_icon = "&#9745;" if challenge["challengeCompleted"] else ""

                row = f"""
                            <tr class="{solved_class}" data-cy="challenge-row">
                                <th scope="row" class="d-none d-xl-table-cell">{i}</th>
                                <td>
                                    &nbsp;<span class="d-xl-none">{solved_icon}</span>
                                    <a href="/challenge/{challenge['link']}">
                                        <span data-cy="{challenge['dataLabel']}">{challenge['name']}</span>
                                    </a>
                                </td>
                                <td>{challenge['tech']}</td>
                                <td class="d-none d-md-table-cell">{challenge['starsOnScale']}</td>
                                <td>{challenge['runtimeEnvironmentCategory']}</td>
                                <td class="d-none d-xl-table-cell">
                                    <span>{solved_icon}</span>
                                </td>
                            </tr>"""
                challenge_rows += row

            # Replace the entire loop with generated content
            pattern = r'<tr th:each="challenge,iter: \$\{challenges\}"[^>]*>.*?</tr>'
            content = re.sub(pattern, challenge_rows, content, flags=re.DOTALL)

        # Handle canary token URLs loop
        if 'th:each="canarytokenURL : ${canarytokenURLs}"' in content:
            canary_items = ""
            for url in self.mock_data["canarytokenURLs"]:
                canary_items += f"<li>{url}</li>\n"

            pattern = r'<ul[^>]*th:each="canarytokenURL : \$\{canarytokenURLs\}"[^>]*>.*?</ul>'
            content = re.sub(
                pattern,
                f'<ul style="word-wrap: break-word">{canary_items}</ul>',
                content,
                flags=re.DOTALL,
            )

        return content

    def replace_th_href(self, content):
        """Replace th:href attributes."""
        content = re.sub(r'th:href="@\{([^}]+)\}"', r'href="\1"', content)
        content = re.sub(
            r'th:href="\$\{swaggerURI\}"',
            f'href="{self.mock_data["swaggerURI"]}"',
            content,
        )
        return content

    def replace_th_attr(self, content):
        """Replace th:attr attributes."""
        content = re.sub(r'th:attr="[^"]*"', "", content)
        return content

    def add_static_assets(self, content, template_name):
        """Add CSS and JS links for the static preview."""
        if "<head>" in content and "bootstrap" not in content:
            head_additions = """
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OWASP WrongSecrets - Preview</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet" />
    <link href="../css/style.css" rel="stylesheet" />
    <link href="../css/dark.css" rel="stylesheet" />
    <link rel="icon" type="image/png" href="../favicon.png">
    <style>
        .preview-banner {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            padding: 10px 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .preview-banner .alert-heading {
            color: #0c5460;
            font-size: 1.1em;
            margin-bottom: 5px;
        }
        .solved { background-color: #d4edda; }
    </style>"""
            content = content.replace("<head>", f"<head>{head_additions}")

        # Add preview banner
        if template_name != "index":
            banner = f"""
    <div class="preview-banner">
        <div class="alert-heading">📋 Static Preview Notice</div>
        <small>This is a static preview of PR #{self.pr_number}. Some dynamic content may be simplified or use mock data.</small>
    </div>"""

            if '<div class="container">' in content:
                content = content.replace(
                    '<div class="container">', f'<div class="container">{banner}'
                )
            elif "<body>" in content:
                content = content.replace(
                    "<body>", f'<body><div class="container">{banner}</div>'
                )

        return content

    def generate_navigation_html(self):
        """Generate a simple navigation for the preview."""
        return """
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="index.html">🔐 OWASP WrongSecrets</a>
            <div class="navbar-nav">
                <a class="nav-link" href="index.html">Home</a>
                <a class="nav-link" href="about.html">About</a>
                <a class="nav-link" href="stats.html">Stats</a>
                <a class="nav-link" href="challenge-example.html">Challenge Example</a>
            </div>
        </div>
    </nav>"""

    def generate_welcome_page(self):
        """Generate the welcome/index page."""
        template_path = self.templates_dir / "welcome.html"

        if not template_path.exists():
            print(f"Warning: Template {template_path} not found")
            return self.generate_fallback_welcome()

        with open(template_path, "r", encoding="utf-8") as f:
            content = f.read()

        # Process the template
        content = self.process_thymeleaf_syntax(content, "welcome")

        # Add navigation
        nav = self.generate_navigation_html()
        content = content.replace("<body>", f"<body>{nav}")

        return content

    def generate_about_page(self):
        """Generate the about page."""
        template_path = self.templates_dir / "about.html"

        if not template_path.exists():
            print(f"Warning: Template {template_path} not found")
            return self.generate_fallback_about()

        with open(template_path, "r", encoding="utf-8") as f:
            content = f.read()

        # Process the template
        content = self.process_thymeleaf_syntax(content, "about")

        # Add navigation
        nav = self.generate_navigation_html()
        content = content.replace("<body>", f"<body>{nav}")

        return content

    def generate_stats_page(self):
        """Generate the stats page."""
        template_path = self.templates_dir / "stats.html"

        if not template_path.exists():
            print(f"Warning: Template {template_path} not found")
            return self.generate_fallback_stats()

        with open(template_path, "r", encoding="utf-8") as f:
            content = f.read()

        # Process the template
        content = self.process_thymeleaf_syntax(content, "stats")

        # Add navigation
        nav = self.generate_navigation_html()
        content = content.replace("<body>", f"<body>{nav}")

        return content

    def generate_challenge_57_preview(self):
        """Generate a specific preview page for Challenge 57 (LLM)."""
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Challenge 57: LLM API Key Exposure - Preview</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet" />
    <link href="../css/style.css" rel="stylesheet" />
    <style>
        .preview-banner { background: #f8f9fa; border: 1px solid #dee2e6; padding: 15px; margin-bottom: 20px; border-radius: 5px; }
        .challenge-card { background: #fff; border: 1px solid #ddd; border-radius: 8px; padding: 20px; margin-bottom: 20px; }
        .code-preview { background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 4px; padding: 15px; font-family: 'Courier New', monospace; font-size: 0.9em; }
        .vulnerability-highlight { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 10px; margin: 10px 0; }
        .tech-badge { background: #007bff; color: white; padding: 4px 8px; border-radius: 12px; font-size: 0.8em; }
        .difficulty-stars { color: #ffc107; }
    </style>
</head>
<body>
    <div class="container mt-4">
        <div class="preview-banner">
            <h4>📋 Challenge 57 Preview</h4>
            <p>This is a static preview of <strong>Challenge 57: LLM API Key Exposure in Client-Side JavaScript</strong></p>
        </div>

        <div class="challenge-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2>🤖 Challenge 57: LLM API Key Exposure</h2>
                <div>
                    <span class="tech-badge">AI</span>
                    <span class="difficulty-stars ms-2">⭐⭐</span>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <h4>📋 Challenge Description</h4>
                    <p>This challenge demonstrates a critical security vulnerability in modern AI-powered web applications: <strong>LLM API keys exposed in client-side JavaScript code</strong>.</p>

                    <p>As developers rush to integrate AI capabilities, many make the critical mistake of putting sensitive API credentials directly in browser-accessible code.</p>

                    <div class="vulnerability-highlight">
                        <strong>⚠️ Vulnerability:</strong> API keys exposed in client-side JavaScript can lead to massive financial losses, service disruption, and data harvesting.
                    </div>

                    <h5>🎯 Your Mission</h5>
                    <p>Find the exposed LLM API key in the client-side JavaScript code. Look for:</p>
                    <ul>
                        <li>API keys starting with "sk-"</li>
                        <li>JavaScript variables storing credentials</li>
                        <li>Console.log statements with sensitive data</li>
                        <li>Authorization headers in network requests</li>
                    </ul>
                </div>

                <div class="col-md-6">
                    <h4>💻 Code Preview</h4>
                    <div class="code-preview">
// AI Chat Application - Client-side JavaScript
class LLMChatApp {
    constructor() {
        // WARNING: This is a security anti-pattern!
        // API keys should NEVER be exposed in client-side code
        this.apiKey = 'sk-llm-api-key-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzA';
        this.apiEndpoint = 'https://api.example-llm.com/v1/chat/completions';
        this.initializeChat();
    }

    async sendMessage() {
        try {
            const response = await fetch(this.apiEndpoint, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.apiKey}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    model: 'gpt-3.5-turbo',
                    messages: [{role: 'user', content: message}]
                })
            });
        } catch (error) {
            console.log('Failed request used API key:', this.apiKey);
        }
    }
}

// Debug: Log the API key for development (another anti-pattern!)
console.log('Debug: LLM API Key = sk-llm-api-key-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzA');
                    </div>

                    <div class="mt-3">
                        <h5>🔍 How to Explore:</h5>
                        <ol>
                            <li>Visit <code>/llm-demo</code> to see the vulnerable chat application</li>
                            <li>Open Developer Tools (F12)</li>
                            <li>Check the <code>/llm-chat.js</code> file</li>
                            <li>Monitor console for debug messages</li>
                            <li>Examine network requests</li>
                        </ol>
                    </div>
                </div>
            </div>

            <div class="mt-4">
                <h4>🏆 Learning Objectives</h4>
                <div class="row">
                    <div class="col-md-4">
                        <strong>💰 Financial Impact</strong>
                        <p>LLM API calls can be extremely expensive. Exposed keys have led to bills of tens of thousands of dollars within hours.</p>
                    </div>
                    <div class="col-md-4">
                        <strong>🔒 Security Risks</strong>
                        <p>Attackers can use your API keys for data harvesting, service disruption, and generating harmful content.</p>
                    </div>
                    <div class="col-md-4">
                        <strong>🛡️ Prevention</strong>
                        <p>Always use server-side proxies, environment variables, and proper access controls for AI service credentials.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>"""

    def generate_challenge_58_preview(self):
        """Generate a specific preview page for Challenge 58 (Database)."""
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Challenge 58: Database Connection String Exposure - Preview</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet" />
    <link href="../css/style.css" rel="stylesheet" />
    <style>
        .preview-banner { background: #f8f9fa; border: 1px solid #dee2e6; padding: 15px; margin-bottom: 20px; border-radius: 5px; }
        .challenge-card { background: #fff; border: 1px solid #ddd; border-radius: 8px; padding: 20px; margin-bottom: 20px; }
        .code-preview { background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 4px; padding: 15px; font-family: 'Courier New', monospace; font-size: 0.9em; }
        .vulnerability-highlight { background: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 10px; margin: 10px 0; }
        .tech-badge { background: #28a745; color: white; padding: 4px 8px; border-radius: 12px; font-size: 0.8em; }
        .difficulty-stars { color: #ffc107; }
        .error-example { background: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 10px; margin: 10px 0; }
    </style>
</head>
<body>
    <div class="container mt-4">
        <div class="preview-banner">
            <h4>📋 Challenge 58 Preview</h4>
            <p>This is a static preview of <strong>Challenge 58: Database Connection String Exposure through Error Messages</strong></p>
        </div>

        <div class="challenge-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2>🗄️ Challenge 58: Database Connection String Exposure</h2>
                <div>
                    <span class="tech-badge">LOGGING</span>
                    <span class="difficulty-stars ms-2">⭐⭐</span>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <h4>📋 Challenge Description</h4>
                    <p>This challenge demonstrates one of the most common and dangerous ways secrets leak in real-world applications: <strong>database connection strings with embedded credentials exposed through error messages</strong>.</p>

                    <p>When applications fail to connect to databases, they often expose the full connection string (including usernames and passwords) in error messages, logs, or even user-facing interfaces.</p>

                    <div class="vulnerability-highlight">
                        <strong>⚠️ Critical Risk:</strong> Database connection string exposure can lead to direct database access and complete data breaches.
                    </div>

                    <h5>🎯 Your Mission</h5>
                    <p>Trigger a database connection error and find the exposed password. Look for:</p>
                    <ul>
                        <li>JDBC connection URLs with embedded credentials</li>
                        <li>Error messages containing connection strings</li>
                        <li>Log entries with sensitive information</li>
                        <li>Patterns like <code>password=SECRET</code></li>
                    </ul>
                </div>

                <div class="col-md-6">
                    <h4>💻 Vulnerable Code Example</h4>
                    <div class="code-preview">
// Simulated database connection string with embedded credentials
private static final String DB_CONNECTION_STRING =
    "jdbc:postgresql://db.example.com:5432/userdb?" +
    "user=dbadmin&password=SuperSecretDB2024!&ssl=true";

public String simulateDatabaseConnectionError() {
    try {
        // This will fail and expose the connection string
        Connection conn = DriverManager.getConnection(DB_CONNECTION_STRING);
        return "Connection successful";
    } catch (SQLException e) {
        // Poor error handling - exposing full connection string
        String errorMessage =
            "Database connection failed with connection string: " +
            DB_CONNECTION_STRING + "\nError: " + e.getMessage();

        // Credentials also get logged (another exposure vector)
        log.error("Failed to connect to database: {}", errorMessage);

        return errorMessage;
    }
}
                    </div>

                    <div class="error-example">
                        <strong>Example Error Output:</strong><br>
                        <code>Database connection failed with connection string: jdbc:postgresql://db.example.com:5432/userdb?user=dbadmin&password=SuperSecretDB2024!&ssl=true</code>
                    </div>
                </div>
            </div>

            <div class="mt-4">
                <h4>🔍 How to Trigger the Error</h4>
                <p>Visit the <code>/error-demo/database-connection</code> endpoint to simulate a database connection failure that exposes credentials in both the HTTP response and application logs.</p>
            </div>

            <div class="mt-4">
                <h4>🏆 Learning Objectives</h4>
                <div class="row">
                    <div class="col-md-4">
                        <strong>🚨 Common Exposure Vectors</strong>
                        <ul class="small">
                            <li>Application startup logs</li>
                            <li>Health check failures</li>
                            <li>CI/CD pipeline logs</li>
                            <li>Error tracking services</li>
                        </ul>
                    </div>
                    <div class="col-md-4">
                        <strong>💥 Real-World Impact</strong>
                        <ul class="small">
                            <li>Production database compromises</li>
                            <li>Complete data breaches</li>
                            <li>Lateral movement attacks</li>
                            <li>Compliance violations</li>
                        </ul>
                    </div>
                    <div class="col-md-4">
                        <strong>🛡️ Prevention</strong>
                        <ul class="small">
                            <li>External secret management</li>
                            <li>Error message sanitization</li>
                            <li>Separate database credentials</li>
                            <li>Connection pooling</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>"""

    def generate_challenge_page(self):
        """Generate an example challenge page."""
        template_path = self.templates_dir / "challenge.html"

        if not template_path.exists():
            print(f"Warning: Template {template_path} not found")
            return self.generate_fallback_challenge()

        with open(template_path, "r", encoding="utf-8") as f:
            content = f.read()

        # Add mock challenge data
        mock_challenge = {
            "name": "Challenge 1: Find the hard-coded password",
            "stars": "⭐",
            "tech": "DEVOPS",
            "explanation": "This is a preview of how a challenge would look.",
            "link": "/challenge/challenge-1",
        }

        # Replace challenge-specific content
        content = re.sub(
            r'th:text="\$\{challenge\.name\}"', f'>{mock_challenge["name"]}<', content
        )
        content = re.sub(
            r'th:text="\$\{challenge\.stars\}"', f'>{mock_challenge["stars"]}<', content
        )
        content = re.sub(
            r'th:text="\$\{challenge\.tech\}"', f'>{mock_challenge["tech"]}<', content
        )

        # Process the template
        content = self.process_thymeleaf_syntax(content, "challenge")

        # Add navigation
        nav = self.generate_navigation_html()
        content = content.replace("<body>", f"<body>{nav}")

        return content

    def generate_fallback_welcome(self):
        """Generate a fallback welcome page if template is missing."""
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OWASP WrongSecrets - Welcome</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>
    <div class="container mt-4">
        <h1>🔐 OWASP WrongSecrets</h1>
        <p class="lead">Learn about secrets management by finding real secrets hidden in code, configuration files, and cloud infrastructure.</p>
        <div class="alert alert-info">
            <strong>Template Preview:</strong> This is a simplified preview. The welcome.html template was not found.
        </div>
    </div>
</body>
</html>"""

    def generate_fallback_about(self):
        """Generate a fallback about page if template is missing."""
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OWASP WrongSecrets - About</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>
    <div class="container mt-4">
        <h1>About WrongSecrets</h1>
        <p>This app teaches secrets management through hands-on challenges.</p>
        <div class="alert alert-info">
            <strong>Template Preview:</strong> This is a simplified preview. The about.html template was not found.
        </div>
    </div>
</body>
</html>"""

    def generate_fallback_stats(self):
        """Generate a fallback stats page if template is missing."""
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OWASP WrongSecrets - Stats</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>
    <div class="container mt-4">
        <h1>Current Stats & Config</h1>
        <p>Session counter: 15</p>
        <p>Canary callbacks: 3</p>
        <div class="alert alert-info">
            <strong>Template Preview:</strong> This is a simplified preview. The stats.html template was not found.
        </div>
    </div>
</body>
</html>"""

    def generate_fallback_challenge(self):
        """Generate a fallback challenge page if template is missing."""
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OWASP WrongSecrets - Challenge</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>
    <div class="container mt-4">
        <h1>Challenge Preview</h1>
        <p>This would show a challenge page with a form to submit answers.</p>
        <div class="alert alert-info">
            <strong>Template Preview:</strong> This is a simplified preview. The challenge.html template was not found.
        </div>
    </div>
</body>
</html>"""

    def generate_all_pages(self):
        """Generate all static pages."""
        # Create pages directory
        pages_dir = self.static_dir / f"pr-{self.pr_number}" / "pages"
        pages_dir.mkdir(parents=True, exist_ok=True)

        pages = {
            "welcome.html": self.generate_welcome_page(),
            "about.html": self.generate_about_page(),
            "stats.html": self.generate_stats_page(),
            "challenge-example.html": self.generate_challenge_page(),
            "challenge-57.html": self.generate_challenge_57_preview(),
            "challenge-58.html": self.generate_challenge_58_preview(),
        }

        for filename, content in pages.items():
            output_path = pages_dir / filename
            with open(output_path, "w", encoding="utf-8") as f:
                f.write(content)
            print(f"Generated {filename}")

        print(f"Generated {len(pages)} static pages in {pages_dir}")
        return pages_dir


def main():
    import sys

    if len(sys.argv) != 2:
        print("Usage: python generate_thymeleaf_previews.py <pr_number>")
        sys.exit(1)

    pr_number = sys.argv[1]

    # Paths
    script_dir = Path(__file__).parent
    repo_root = script_dir.parent.parent
    templates_dir = repo_root / "src" / "main" / "resources" / "templates"
    static_dir = Path("static-site")

    print(f"Generating Thymeleaf previews for PR #{pr_number}")
    print(f"Templates directory: {templates_dir}")
    print(f"Output directory: {static_dir}")

    # Create converter and generate pages
    converter = ThymeleafToStaticConverter(templates_dir, static_dir, pr_number)
    pages_dir = converter.generate_all_pages()

    print(f"✅ Successfully generated Thymeleaf previews in {pages_dir}")


if __name__ == "__main__":
    main()
