# Development Patterns

This document outlines common code patterns, conventions, and best practices used throughout the WrongSecrets project.

## Related documentation

- [Spring Boot 4 Adoption Checklist](SPRING_BOOT_4_ADOPTION_CHECKLIST.md)

## Challenge Structure Patterns

### Challenge Interface vs FixedAnswerChallenge

The project uses two main patterns for implementing challenges:

#### 1. Challenge Interface (Dynamic Answers)

For challenges where the answer depends on calculation or user input:

```java
public interface Challenge {
    Spoiler spoiler();                    // Returns the secret
    boolean answerCorrect(String answer); // Validates user answer
}
```

**Use when:**
- Answer requires computation or external data
- Answer changes based on environment or input
- Complex validation logic is needed

#### 2. FixedAnswerChallenge (Static Answers)

For challenges with predetermined, unchanging answers:

```java
public abstract class FixedAnswerChallenge implements Challenge {
    // Caches the answer for performance
    private Supplier<String> cachedAnswer = Suppliers.memoize(() -> getAnswer());

    protected abstract String getAnswer(); // Implement to return fixed answer
}
```

**Use when:**
- Answer is hardcoded or from environment variables
- Answer doesn't change during application runtime
- Simple string comparison validation

### Challenge Implementation Pattern

All challenges follow this structure:

```java
@Component
public class Challenge[Number] extends FixedAnswerChallenge {

    private final RuntimeEnvironment runtimeEnvironment;

    public Challenge[Number](RuntimeEnvironment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }

    @Override
    public String getAnswer() {
        // Implementation specific to challenge
    }

    @Override
    public boolean canRunInCTFMode() {
        return true; // or false based on challenge requirements
    }

    @Override
    public RuntimeEnvironment.Environment supportedRuntimeEnvironments() {
        return RuntimeEnvironment.Environment.DOCKER; // or appropriate environment
    }
}
```

### Challenge Categories by Package

- **`challenges/docker/`** - Challenges specific to Docker environment
- **`challenges/cloud/`** - Cloud provider challenges (AWS, GCP, Azure)
- **`challenges/kubernetes/`** - Kubernetes and Vault integration challenges

## Configuration Management Approach

### Environment-Specific Configuration

The project uses a layered configuration approach:

#### 1. Base Configuration
- `application.properties` - Core Spring Boot settings
- `application-[profile].properties` - Environment-specific overrides

#### 2. External Configuration Sources
- Environment variables (12-factor app approach)
- Kubernetes ConfigMaps and Secrets
- Cloud provider parameter stores (AWS SSM, GCP Secret Manager, Azure Key Vault)

#### 3. Runtime Environment Detection

```java
@Component
public class RuntimeEnvironment {
    public enum Environment {
        DOCKER, AWS, GCP, AZURE, K8S
    }

    public Environment getCurrentEnvironment() {
        // Auto-detection logic based on environment variables
    }
}
```

### Configuration Pattern Example

```java
@Component
public class ExampleChallenge extends FixedAnswerChallenge {

    @Value("${challenge.secret:default-value}")
    private String secret;

    private final RuntimeEnvironment runtimeEnvironment;

    @Override
    public String getAnswer() {
        return switch (runtimeEnvironment.getCurrentEnvironment()) {
            case AWS -> getFromParameterStore();
            case GCP -> getFromSecretManager();
            case AZURE -> getFromKeyVault();
            default -> secret; // fallback to configured value
        };
    }
}
```

## Testing Patterns

### Unit Test Pattern

```java
@ExtendWith(MockitoExtension.class)
class Challenge[Number]Test {

    @Mock
    private RuntimeEnvironment runtimeEnvironment;

    private Challenge[Number] challenge;

    @BeforeEach
    void setUp() {
        challenge = new Challenge[Number](runtimeEnvironment);
    }

    @Test
    void shouldReturnCorrectAnswer() {
        // Given
        when(runtimeEnvironment.getCurrentEnvironment())
            .thenReturn(RuntimeEnvironment.Environment.DOCKER);

        // When
        String answer = challenge.spoiler().solution();

        // Then
        assertThat(answer).isEqualTo("expected-answer");
        assertTrue(challenge.answerCorrect("expected-answer"));
    }
}
```

### Integration Test Pattern

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChallengeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnChallengeResponse() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/challenge/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## Workflow Patterns (GitHub Actions)

### Common Workflow Structure

All workflows follow this pattern:

```yaml
name: [Workflow Name]
on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  [job-name]:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache Maven dependencies
        uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}

      - name: [Specific Action]
        run: [commands]
```

### Key Workflow Categories

1. **Build & Test** (`main.yml`)
   - Compilation verification
   - Unit and integration tests
   - Code quality checks

2. **Security Scanning** (`scanners.yml`, `codeql-analysis.yml`)
   - SAST/DAST security analysis
   - Dependency vulnerability scanning
   - CodeQL analysis

3. **Container Testing** (`container_test.yml`, `container-alts-test.yml`)
   - Docker image building and testing
   - Multi-platform container validation

4. **Deployment Testing** (`heroku_tests.yml`, `minikube-k8s-test.yml`)
   - Platform-specific deployment validation
   - Kubernetes deployment testing

### Version Synchronization Pattern

Automated version management across files:

```yaml
- name: Extract version from pom.xml
  id: extract-version
  run: |
    VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
    echo "VERSION=${VERSION%-SNAPSHOT}" >> $GITHUB_OUTPUT

- name: Use version in Docker build
  run: |
    docker build --build-arg argVersion=${{ steps.extract-version.outputs.VERSION }} .
```

## Code Quality Patterns

### Checkstyle Configuration

- Line length: 100 characters
- Indentation: 2 spaces
- Import organization: Java standard, third-party, static
- Javadoc required for public methods

### PMD Rules

- Complexity thresholds enforced
- Dead code detection
- Security rule enforcement
- Performance anti-patterns detection

### Pre-commit Hooks

1. **Code Formatting** - Google Java Format
2. **Linting** - ESLint for JavaScript/TypeScript
3. **Security** - Git secrets scanning
4. **Commit Messages** - Conventional commits format

## Error Handling Patterns

### Challenge Error Handling

```java
@Override
public String getAnswer() {
    try {
        return retrieveSecret();
    } catch (Exception e) {
        log.error("Failed to retrieve secret for challenge", e);
        throw new ChallengeConfigurationException(
            "Challenge misconfigured: " + e.getMessage(), e);
    }
}
```

### Global Exception Handling

```java
@ControllerAdvice
public class AllControllerAdvice {

    @ExceptionHandler(ChallengeConfigurationException.class)
    public ResponseEntity<String> handleChallengeConfigError(
            ChallengeConfigurationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Challenge configuration error");
    }
}
```

## Naming Conventions

### Challenge Naming
- Class: `Challenge[Number]` (e.g., `Challenge44`)
- Test: `Challenge[Number]Test` (e.g., `Challenge44Test`)
- Endpoint: `/challenge/[number]` (e.g., `/challenge/44`)

### Package Naming
- Environment-based: `challenges.[environment]` (e.g., `challenges.kubernetes`)
- Feature-based: Clear, descriptive names (e.g., `oauth`, `asciidoc`)

### Configuration Naming
- Properties: `challenge.[feature].[property]` (e.g., `challenge.vault.enabled`)
- Environment variables: `CHALLENGE_[FEATURE]_[PROPERTY]` (e.g., `CHALLENGE_VAULT_ENABLED`)

## Documentation Patterns

### Challenge Documentation
Each challenge should include:
- Clear problem description
- Hints for solving
- Learning objectives
- Related security concepts

### Code Documentation
- Public APIs require Javadoc
- Complex logic requires inline comments
- Configuration options documented in properties files
- README files for deployment-specific instructions
