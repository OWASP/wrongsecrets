# Architecture Overview

This document provides a quick reference for understanding the WrongSecrets project structure, testing patterns, build process, and key configuration files.

## Project Structure

### Core Package Organization

```
src/main/java/org/owasp/wrongsecrets/
├── challenges/                    # Challenge implementations and controllers
│   ├── cloud/                    # Cloud provider specific challenges (AWS, GCP, Azure)
│   ├── docker/                   # Docker-based challenges
│   ├── kubernetes/               # Kubernetes and Vault challenges
│   ├── Challenge.java            # Core challenge interface
│   ├── FixedAnswerChallenge.java # Abstract class for static answer challenges
│   └── ChallengesController.java # REST API endpoints for challenges
├── oauth/                        # OAuth authentication components
├── asciidoc/                     # Documentation generation utilities
├── canaries/                     # Security canary implementations
├── definitions/                  # Challenge definitions and metadata
└── [Core Application Files]      # Main application, security config, etc.
```

### Key Responsibilities by Package

- **`challenges/`** - All challenge logic, grouped by deployment technology
- **`oauth/`** - GitHub OAuth integration for user authentication
- **`asciidoc/`** - AsciiDoc documentation generation and processing
- **`canaries/`** - Security monitoring and detection mechanisms
- **`definitions/`** - Challenge metadata, descriptions, and configuration

## Testing Patterns

### Test Organization

```
src/test/java/org/owasp/wrongsecrets/
├── challenges/                   # Challenge-specific unit tests
│   ├── cloud/                   # Cloud challenge tests
│   ├── docker/                  # Docker challenge tests
│   └── kubernetes/              # Kubernetes challenge tests
├── ChallengesControllerTest.java # API endpoint tests
├── SecurityConfigTest.java      # Security configuration tests
└── [Other Component Tests]      # Individual component unit tests
```

### Test Types

1. **Unit Tests** - Individual challenge logic testing (74+ test files)
2. **Integration Tests** - Controller and API endpoint testing
3. **E2E Tests** - Cypress tests in `src/test/e2e/cypress/`
4. **Container Tests** - Docker and Kubernetes deployment validation

### Test Naming Convention

- Challenge tests: `Challenge[Number]Test.java` (e.g., `Challenge44Test.java`)
- Controller tests: `[Controller]Test.java` (e.g., `ChallengesControllerTest.java`)
- Component tests: `[Component]Test.java`

## Build Process Overview

### Maven → Docker Workflow

1. **Maven Build** (`pom.xml`)
   - Spring Boot 3.x application
   - Dependencies managed through Spring Boot parent POM
   - Plugins: AsciiDoctor, Checkstyle, PMD, SpotBugs

2. **Docker Images**
   - `Dockerfile` - Main application container
   - `Dockerfile.web` - Web-only variant (no vault dependencies)
   - `Dockerfile_webdesktop` - Desktop application variant
   - `Dockerfile_webdesktopk8s` - Kubernetes desktop variant

3. **Build Commands**
   ```bash
   ./mvnw clean compile           # Compile sources
   ./mvnw test                    # Run unit tests
   ./mvnw package                 # Create JAR
   docker build -t wrongsecrets . # Build container
   ```

### Version Management

- Version defined in `pom.xml` and synchronized across Dockerfiles
- Automated version extraction in GitHub Actions
- Snapshot versions for development, release versions for production

## Key Configuration Files

### Application Configuration

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build configuration, dependencies, plugins |
| `src/main/resources/application.properties` | Spring Boot application configuration |
| `config/fbctf.yml` | Facebook CTF integration configuration |

### Code Quality & Standards

| File | Purpose |
|------|---------|
| `config/checkstyle/` | Java code style rules and enforcement |
| `config/zap/` | OWASP ZAP security scanning configuration |
| `.pre-commit-config.yaml` | Pre-commit hooks for code quality |
| `eslint.config.mjs` | JavaScript/TypeScript linting rules |

### CI/CD Configuration

| File | Purpose |
|------|---------|
| `.github/workflows/` | GitHub Actions workflow definitions |
| `renovate.json` | Automated dependency updates |
| `commitlint.config.js` | Commit message format enforcement |

### Deployment Configuration

| File | Purpose |
|------|---------|
| `heroku.yml` | Heroku deployment configuration |
| `fly.toml` | Fly.io deployment configuration |
| `render.yaml` | Render.com deployment configuration |
| `app.json` | Heroku app configuration |
| `k8s/` | Kubernetes deployment manifests |

### Platform-Specific

| Directory | Purpose |
|-----------|---------|
| `aws/` | AWS-specific deployment files and documentation |
| `gcp/` | Google Cloud Platform deployment configuration |
| `azure/` | Microsoft Azure deployment setup |
| `okteto/` | Okteto Kubernetes platform configuration |

## Development Environment Setup

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker
- Node.js (for frontend dependencies)

### Quick Setup

```bash
# Clone and build
git clone <repository>
cd wrongsecrets
./mvnw clean compile

# Run locally
./mvnw spring-boot:run

# Run tests
./mvnw test
```

For detailed setup instructions, see [CONTRIBUTING.md](../CONTRIBUTING.md).