# OWASP WrongSecrets - Copilot Instructions

Welcome to the OWASP WrongSecrets project! This document provides GitHub Copilot with essential context to help you contribute effectively to this educational cybersecurity project.

## Project Overview

**WrongSecrets** is an intentionally vulnerable Spring Boot application designed to teach secrets management through real-world examples of what NOT to do. The project contains 56+ challenges where developers must find exposed secrets in code, configuration files, containers, and cloud deployments.

**⚠️ Important**: This repository contains **intentionally vulnerable code** for educational purposes. When contributing, maintain this educational intent while following secure coding practices in the framework itself.

## Repository Structure

```
wrongsecrets/
├── src/main/java/org/owasp/wrongsecrets/
│   ├── challenges/           # Challenge implementations
│   │   ├── docker/          # Docker-specific challenges
│   │   ├── cloud/           # Cloud provider challenges (AWS/GCP/Azure)
│   │   ├── kubernetes/      # K8s and Vault challenges
│   │   └── FixedAnswerChallenge.java  # Base class for simple challenges
│   ├── definitions/         # Challenge metadata and configuration
│   ├── oauth/              # Authentication components
│   └── WrongSecretsApplication.java  # Main Spring Boot application
├── src/test/java/           # Test files mirroring main structure
├── src/main/resources/      # Configuration files and web assets
├── .github/workflows/       # CI/CD pipelines
├── docs/                    # Project documentation
└── k8s/, aws/, gcp/, azure/ # Deployment configurations
```

## Technology Stack

- **Framework**: Spring Boot 3.5.x
- **Java Version**: 23 (configured in pom.xml)
- **Build Tool**: Maven (use `./mvnw`)
- **Testing**: JUnit 5, Spring Boot Test
- **Container**: Docker + Kubernetes
- **Cloud**: AWS, GCP, Azure integrations
- **Frontend**: Thymeleaf templates, Bootstrap CSS

## Development Patterns

### Challenge Implementation

**For challenges with fixed answers** (most common):

```java
@Component
public class Challenge[Number] extends FixedAnswerChallenge {

    private final RuntimeEnvironment runtimeEnvironment;

    public Challenge[Number](RuntimeEnvironment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }

    @Override
    public String getAnswer() {
        // Return the secret that users need to find
        return "the-secret-value";
    }

    @Override
    public boolean canRunInCTFMode() {
        return true; // Set to false if challenge can't run in CTF mode
    }

    @Override
    public RuntimeEnvironment.Environment supportedRuntimeEnvironments() {
        return RuntimeEnvironment.Environment.DOCKER; // or ALL, K8S, etc.
    }
}
```

**For challenges with dynamic answers**:

```java
@Component
public class Challenge[Number] implements Challenge {

    @Override
    public Spoiler spoiler() {
        return new Spoiler(calculateAnswer());
    }

    @Override
    public boolean answerCorrect(String answer) {
        return Objects.equals(calculateAnswer(), answer);
    }

    private String calculateAnswer() {
        // Complex logic here
    }
}
```

### Testing Patterns

**Challenge tests should**:

```java
@ExtendWith(MockitoExtension.class)
class Challenge[Number]Test {

    @Mock
    private RuntimeEnvironment runtimeEnvironment;

    @InjectMocks
    private Challenge[Number] challenge;

    @Test
    void answerCorrect() {
        // Test the correct answer
        assertTrue(challenge.answerCorrect(challenge.spoiler().solution()));
    }

    @Test
    void answerIncorrect() {
        // Test incorrect answers
        assertFalse(challenge.answerCorrect("wrong-answer"));
    }

    @Test
    void canRunInCTFMode() {
        // Test CTF mode support
        assertTrue(challenge.canRunInCTFMode());
    }
}
```

## Code Style Guidelines

### Java Conventions

- **Package naming**: Follow existing structure under `org.owasp.wrongsecrets`
- **Class naming**:
  - Challenges: `Challenge[Number]` (e.g., `Challenge1`, `Challenge42`)
  - Tests: `Challenge[Number]Test`
- **Formatting**: Use standard Spring Boot/Google Java style
- **Imports**: Avoid wildcard imports, group logically
- **Documentation**: JavaDoc for public APIs, inline comments for complex logic

### Spring Boot Patterns

- **Configuration**: Use `@ConfigurationProperties` for external config
- **Profiles**: Support `docker`, `k8s`, `aws`, `gcp`, `azure` profiles
- **Components**: Use `@Component`, `@Service`, `@Controller` appropriately
- **Environment**: Access via `RuntimeEnvironment` service, not direct `@Value`

### Security Considerations

**When writing framework code** (not challenge vulnerabilities):

- ✅ Use parameterized queries
- ✅ Validate and sanitize inputs
- ✅ Implement proper authentication/authorization
- ✅ Follow OWASP secure coding practices
- ✅ Use secure random generators
- ✅ Avoid hardcoded credentials in framework code

**When creating challenge vulnerabilities** (educational content):

- ✅ Document the vulnerability type clearly
- ✅ Ensure vulnerability is realistic and educational
- ✅ Include hints and explanations for learners
- ✅ Make secrets discoverable but not trivial

## Common Tasks

### Adding a New Challenge

1. **Determine challenge type**: Fixed answer or dynamic?
2. **Choose appropriate package**: `docker/`, `cloud/`, `kubernetes/`
3. **Implement challenge class**: Extend `FixedAnswerChallenge` or implement `Challenge`
4. **Add corresponding test**: Mirror the package structure in `src/test/`
5. **Update configuration**: Add to `ChallengeDefinitionsConfiguration` if needed
6. **Document**: Add challenge description and hints

### Running the Application

```bash
# Build and compile
./mvnw clean compile

# Run tests
./mvnw test

# Run application locally
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring.profiles.active=docker

# Run specific test
./mvnw test -Dtest=Challenge1Test
```

### Docker Commands

```bash
# Build container
docker build -t wrongsecrets .

# Run locally
docker run -p 8080:8080 -p 8090:8090 wrongsecrets
```

## Testing Guidelines

- **Unit tests**: Test challenge logic, answer validation
- **Integration tests**: Test Spring context, controller endpoints
- **E2E tests**: Cypress tests for UI interactions
- **Profile tests**: Test different environment configurations

### Test Structure

```java
// Arrange
when(runtimeEnvironment.getRuntimeEnvironment())
    .thenReturn(RuntimeEnvironment.Environment.DOCKER);

// Act
String result = challenge.getAnswer();

// Assert
assertThat(result).isEqualTo("expected-secret");
```

## Environment Variables

Common environment variables used in challenges:

- `SPRING_PROFILES_ACTIVE`: Runtime environment (docker, k8s, aws, etc.)
- `K8S_ENV`: Kubernetes environment flag
- `wrongsecret[number]`: Challenge-specific secrets
- Cloud-specific: AWS/GCP/Azure credentials and configurations

## Performance Considerations

- **Fixed challenges**: Use caching via `FixedAnswerChallenge`
- **Heavy operations**: Cache results, use lazy initialization
- **External calls**: Mock in tests, handle failures gracefully
- **Memory**: Be mindful of static collections and caching

## Documentation

- **README.md**: Keep setup instructions current
- **CONTRIBUTING.md**: Follow established patterns
- **JavaDoc**: Document public APIs and complex logic
- **Challenge hints**: Provide educational guidance without giving away answers

## Common Pitfalls to Avoid

- ❌ Don't expose real secrets in challenge code
- ❌ Don't break existing challenge numbering
- ❌ Don't hardcode environment-specific values
- ❌ Don't skip tests for new functionality
- ❌ Don't ignore existing code patterns
- ❌ Don't modify unrelated challenges when adding new ones

## Getting Help

- **Documentation**: Check `docs/` directory for detailed guides
- **Existing code**: Look at similar challenges for patterns
- **Tests**: Examine test files for usage examples
- **GitHub Issues**: Search existing issues and discussions
- **OWASP Slack**: Join #project-wrongsecrets channel

Remember: This is an educational project teaching security through intentionally vulnerable examples. Balance realistic vulnerabilities with clear learning outcomes while keeping the framework itself secure and maintainable.
