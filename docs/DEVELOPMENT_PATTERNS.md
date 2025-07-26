# Development Patterns

This document outlines common development patterns and conventions used throughout the WrongSecrets project.

## Challenge Development Patterns

### When to Use FixedAnswerChallenge vs Challenge Interface

**Use FixedAnswerChallenge when:**
- The challenge has a static, predetermined answer
- No external dependencies (files, services, etc.)
- Simple validation logic

```java
@Component
public class Challenge1 extends FixedAnswerChallenge {
    private final String secret = "defaultsecret";
    
    public String getAnswer() {
        return secret;
    }
}
```

**Use Challenge Interface when:**
- Dynamic answer generation
- External dependencies (files, environment variables, services)
- Complex validation logic
- Multi-step challenges

```java
@Component  
public class Challenge36 implements Challenge {
    public boolean solved(String answer) {
        // Custom validation logic
        return processExternalDependency(answer);
    }
    
    public Spoiler spoiler() {
        return new Spoiler(generateDynamicAnswer());
    }
}
```

### Challenge Naming Conventions
- Class names: `Challenge{N}` where N is the challenge number
- Package organization by environment: `docker`, `kubernetes`, `aws`, etc.
- Test classes: `Challenge{N}Test`

### Required Challenge Methods
```java
// All challenges must implement:
public boolean solved(String answer)  // Validation logic
public Spoiler spoiler()             // Solution with explanation
public List<RuntimeEnvironment> supportedRuntimeEnvironments()  // Where it runs
public int difficulty()              // Difficulty level (1-5)
public String getTech()             // Technology category
```

## Configuration Patterns

### Environment-Specific Configuration

**File naming pattern:**
- `application.properties` - Base configuration
- `application-{profile}.properties` - Profile-specific overrides
- `application-{profile}-vault.properties` - Vault-enabled variants

**Profile activation:**
```bash
# Local development
--spring.profiles.active=local,without-vault

# Docker environment  
--spring.profiles.active=docker,without-vault

# Kubernetes with vault
--spring.profiles.active=k8s,vault
```

### Challenge Configuration (YAML)

**Standard pattern:**
```yaml
- name: Challenge N
  url: "challenge-n"
  sources:
    - class-name: "org.owasp.wrongsecrets.challenges.package.ChallengeN"
      explanation: "explanations/challengeN.adoc"
      hint: "explanations/challengeN_hint.adoc"  
      reason: "explanations/challengeN_reason.adoc"
      environments: *docker_envs  # Reference to environment list
  difficulty: *easy  # Reference to difficulty constant
  category: *secrets  # Reference to category constant
  ctf:
    enabled: true
```

**Environment references:**
```yaml
# Define once, reference everywhere
docker_envs: &docker_envs
  - DOCKER
  - DOCKER_NO_VAULT

easy: &easy 1
medium: &medium 2
hard: &hard 3
```

## Testing Patterns

### Unit Test Structure
```java
class ChallengeNTest {
    
    @Test
    void rightAnswerShouldSolveChallenge() {
        var challenge = new ChallengeN();
        
        // Test wrong answers return false
        Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
        
        // Test correct answer returns true (using spoiler to avoid hardcoding)
        Assertions.assertThat(challenge.solved(challenge.spoiler().solution())).isTrue();
    }
    
    @Test  
    void shouldReturnCorrectSpoiler() {
        var challenge = new ChallengeN();
        var spoiler = challenge.spoiler();
        
        Assertions.assertThat(spoiler).isNotNull();
        Assertions.assertThat(spoiler.solution()).isNotEmpty();
    }
}
```

### Integration Test Patterns
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChallengeControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldReturnChallengeData() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/challenge/1", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("challenge content");
    }
}
```

### Test Data Management
- **Never hardcode secrets** in tests
- **Use challenge.spoiler().solution()** to get correct answers
- **Mock external dependencies** for unit tests
- **Use @TestConfiguration** for test-specific Spring configuration

## Docker and Build Patterns

### Dockerfile Conventions

**Version management:**
```dockerfile
# Use build arg for version (never hardcode)
ARG argBasedVersion
COPY target/wrongsecrets-${argBasedVersion}.jar application.jar
```

**Multi-stage builds:**
```dockerfile
# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build-env
# Build logic here

# Runtime stage  
FROM eclipse-temurin:21-jre-alpine
# Runtime setup here
```

### Maven Build Patterns

**Version extraction in CI:**
```bash
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
DOCKER_VERSION=${VERSION%-SNAPSHOT}
```

**Profile-specific builds:**
```bash
# Local development build
mvn clean package -P local

# Production build with security scanning
mvn clean package -P production
```

## GitHub Actions Patterns

### Workflow Structure
```yaml
name: Descriptive Workflow Name

on:
  push:
    branches: [master]
  pull_request:
    branches: [master]
    
permissions:
  contents: read
  packages: write  # Only if publishing containers

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 23
        uses: actions/setup-java@v4
        with:
          java-version: '23'
          distribution: 'temurin'
```

### Version Handling in Workflows
```yaml
- name: Extract version from pom.xml
  id: extract-version  
  run: |
    VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
    DOCKER_VERSION=${VERSION%-SNAPSHOT}
    echo "docker_version=$DOCKER_VERSION" >> $GITHUB_OUTPUT

- name: Build Docker image
  run: |
    docker build \
      --build-arg argBasedVersion="${{ steps.extract-version.outputs.docker_version }}" \
      -t wrongsecrets .
```

### Conditional Job Execution
```yaml
# Only run on specific file changes
on:
  push:
    paths:
      - 'src/**'
      - 'pom.xml'
      - 'Dockerfile*'

# Only for specific environments
if: github.ref == 'refs/heads/master'
```

## Code Quality Patterns

### Pre-commit Hook Integration
```yaml
# .pre-commit-config.yaml patterns
repos:
  - repo: local
    hooks:
      - id: java-formatting
        name: Format Java code
        entry: ./scripts/format-java.sh
        language: script
        files: \.java$
```

### Checkstyle Configuration
- **Follow Google Java Style** with project-specific exceptions
- **Line length: 120 characters**
- **No wildcard imports**
- **Consistent indentation: 4 spaces**

### PMD Rules
- **Avoid duplicated code blocks**
- **Enforce meaningful variable names**
- **Limit method complexity**
- **No unused imports or variables**

## Security Patterns

### Secrets Management
```java
// Good: Use Spring's @Value with defaults
@Value("${secret.key:default-value}")
private String secretKey;

// Bad: Hardcoded secrets
private String secretKey = "actual-secret-value";
```

### Input Validation
```java
// Validate all user inputs
public boolean solved(String answer) {
    if (answer == null || answer.trim().isEmpty()) {
        return false;
    }
    
    // Sanitize input before processing
    String sanitized = answer.trim().toLowerCase();
    return validateAnswer(sanitized);
}
```

### Error Handling
```java
// Don't leak sensitive information in error messages
try {
    processSecret(input);
} catch (Exception e) {
    log.warn("Challenge validation failed", e);
    return false;  // Generic response
}
```

## Documentation Patterns

### AsciiDoc Structure
```asciidoc
=== Challenge Title

Brief description of what the challenge teaches.

==== Learning Objectives
* Objective 1
* Objective 2

==== Challenge Details
Detailed explanation...

==== Hints
<<< Spoiler alert - solution below >>>

Solution explanation...
```

### README Updates
- **Keep examples current** with latest Docker images
- **Include both stable and development versions**
- **Provide clear installation instructions**
- **Document environment variables and configuration**

## Error Prevention Patterns

### Common Pitfalls to Avoid
1. **Hardcoding versions** in Dockerfiles or workflows
2. **Exposing real secrets** in test code or documentation
3. **Missing unit tests** for new challenges
4. **Inconsistent package naming** across challenge types
5. **Not updating configuration YAML** when adding challenges

### Validation Checklist
- [ ] Version consistency across all files
- [ ] Unit tests pass and cover new functionality
- [ ] Pre-commit hooks pass locally
- [ ] Documentation is updated
- [ ] No hardcoded secrets or sensitive data
- [ ] Challenge configuration is properly formatted

These patterns help maintain code quality, consistency, and security throughout the WrongSecrets project.