# Architecture Overview

This document provides a quick architectural overview of the WrongSecrets project to help contributors and AI agents understand the codebase structure.

## Project Structure

### Core Application (`src/main/java/org/owasp/wrongsecrets/`)

```
├── challenges/           # Challenge implementations
│   ├── docker/          # Docker-specific challenges
│   ├── kubernetes/      # Kubernetes-specific challenges
│   ├── Challenge.java   # Base challenge interface
│   └── FixedAnswerChallenge.java  # Simple challenge base class
├── config/              # Spring configuration classes
├── controllers/         # REST API controllers
├── services/            # Business logic services
└── WrongSecretsApplication.java  # Main Spring Boot application
```

### Resources (`src/main/resources/`)

```
├── explanations/        # Challenge explanations (.adoc files)
├── static/             # Static web assets (CSS, JS, images)
├── templates/          # Thymeleaf HTML templates
└── wrong_secrets_configuration.yaml  # Challenge configuration
```

### Testing (`src/test/java/`)

```
├── challenges/         # Challenge unit tests
├── controllers/        # Controller integration tests
└── WrongSecretsApplicationTests.java  # Main application tests
```

## Key Architectural Patterns

### Challenge System

**Two main challenge types:**

1. **FixedAnswerChallenge** - For simple challenges with static answers
   ```java
   @Component
   public class Challenge1 extends FixedAnswerChallenge {
       public String getAnswer() { return "secret"; }
   }
   ```

2. **Challenge Interface** - For complex challenges with external dependencies
   ```java
   @Component
   public class Challenge36 implements Challenge {
       public boolean solved(String answer) { /* custom logic */ }
   }
   ```

### Configuration Management

- **Environment-specific configs**: `application.properties`, `application-docker.properties`, etc.
- **Challenge config**: `wrong_secrets_configuration.yaml` defines all challenges
- **Spring profiles**: Used for different deployment environments (`local`, `docker`, `k8s`)

### Build and Deployment

**Maven → Docker Pipeline:**
1. `mvn package` creates JAR file
2. Dockerfile copies JAR and sets up runtime
3. Multiple Dockerfiles for different variants (`Dockerfile`, `Dockerfile.web`)

**Version Management:**
- Single source of truth: `pom.xml` version
- Automated sync to Dockerfile build args
- Scripts in `/scripts/` for version management

## Testing Strategy

### Unit Tests
- **Location**: `src/test/java/` mirrors main package structure
- **Pattern**: Each challenge has corresponding test class
- **Example**: `Challenge1Test.java` tests `Challenge1.java`

### Integration Tests
- **Controllers**: Test REST API endpoints
- **Application**: Test Spring Boot application startup
- **Docker**: Container-based testing via GitHub Actions

### Required Test Patterns
- All challenges must have unit tests
- Tests use `.spoiler().solution()` to get correct answers
- No tests should hardcode secrets (use challenge methods)

## CI/CD Architecture

### GitHub Actions Workflows

**Core Workflows:**
- `main.yml` - Main build and test pipeline
- `container_test.yml` - Docker container testing
- `pre-commit.yml` - Code quality checks
- `pr-preview.yml` - PR preview deployments

**Security Workflows:**
- `codeql-analysis.yml` - Code security analysis
- `scanners.yml` - Dependency vulnerability scanning
- `dast-zap-test.yml` - Dynamic application security testing

**Deployment Workflows:**
- `master-container-publish.yml` - Auto-publish master containers
- `heroku_tests.yml` - Heroku deployment testing

### Version Synchronization
- Automated version extraction from `pom.xml`
- Dynamic Docker build arguments
- CI validation prevents version drift

## External Integrations

### Container Registries
- **Docker Hub**: `jeroenwillemsen/wrongsecrets`
- **GitHub Container Registry**: `ghcr.io/owasp/wrongsecrets/wrongsecrets-master`

### Cloud Platforms
- **AWS**: Challenges and deployment configs in `/aws/`
- **Azure**: Resources in `/azure/`
- **GCP**: Resources in `/gcp/`
- **Kubernetes**: Manifests in `/k8s/`

### Development Tools
- **Pre-commit hooks**: Code formatting and linting
- **Renovate**: Automated dependency updates
- **CodeClimate**: Code quality analysis

## Common Development Tasks

### Adding a New Challenge
1. Create challenge class in appropriate package
2. Add unit test in corresponding test package
3. Create explanation files (.adoc) in `/src/main/resources/explanations/`
4. Update `wrong_secrets_configuration.yaml`

### Running Locally
```bash
# Set environment variables
export K8S_ENV=docker

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=local,without-vault
```

### Building Docker Images
```bash
# Extract version from pom.xml
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
DOCKER_VERSION=${VERSION%-SNAPSHOT}

# Build with correct version
docker build --build-arg argBasedVersion="$DOCKER_VERSION" -t wrongsecrets .
```

### Running Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Specific test class
mvn test -Dtest=Challenge1Test
```

## Configuration Files Reference

| File | Purpose |
|------|---------|
| `pom.xml` | Maven project configuration and dependencies |
| `application.properties` | Spring Boot base configuration |
| `wrong_secrets_configuration.yaml` | Challenge definitions and metadata |
| `.pre-commit-config.yaml` | Code quality and formatting rules |
| `Dockerfile` | Main container build instructions |
| `Dockerfile.web` | Web-optimized container variant |

## Key Dependencies

### Spring Boot Ecosystem
- **Spring Web**: REST API and web interface
- **Spring Security**: Authentication and authorization
- **Thymeleaf**: Server-side templating
- **Spring Boot Actuator**: Health checks and metrics

### Testing
- **JUnit 5**: Unit testing framework
- **AssertJ**: Fluent assertions
- **Spring Boot Test**: Integration testing support

### Build and Quality
- **Maven**: Build automation
- **Checkstyle**: Code style enforcement
- **PMD**: Static code analysis
- **SpotBugs**: Bug pattern detection

This overview should help new contributors and AI agents quickly understand the project structure and development patterns.