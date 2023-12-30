package org.owasp.wrongsecrets;

import io.github.wimdeblauwe.testcontainers.cypress.CypressContainer;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTest;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTestResults;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTestSuite;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.owasp.wrongsecrets.asciidoc.AsciiDocGenerator;
import org.owasp.wrongsecrets.asciidoc.PreCompiledGenerator;
import org.owasp.wrongsecrets.asciidoc.TemplateGenerator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.testcontainers.Testcontainers;

/**
 * Running Cypress tests as a normal unit test with Testcontainers. First it start the application
 * as a normal Spring Boot application, then it starts the Cypress container and runs the tests
 * inside the container. Afterward, the test results are converted to JUnit tests.
 *
 * <p>One main advantage of this approach is that you can debug the application directly by
 * debugging this test. And it offers the option to have multiple tests with multiple configuration
 * of the WrongSecrets application.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "CHALLENGEDOCKERMTPATH=src/test/resources/",
      "challengedockermtpath>src/test/resources/",
      "KEEPASSPATH=src/test/resources/alibabacreds.kdbx",
      "keepasspath=src/test/resources/alibabacreds.kdbx"
    })
@Slf4j
@Import(CypressIntegrationTest.Configuration.class)
public class CypressIntegrationTest {
  @LocalServerPort private int port;

  @TestFactory
  List<DynamicContainer> runCypressTests()
      throws InterruptedException, IOException, TimeoutException {

    Testcontainers.exposeHostPorts(port);

    try (CypressContainer container = new CypressContainer().withLocalServerPort(port)) {
      container.withMaximumTotalTestDuration(Duration.ofMinutes(15));

      container.start();
      CypressTestResults testResults = container.getTestResults();

      return convertToJUnitDynamicTests(testResults);
    }
  }

  private List<DynamicContainer> convertToJUnitDynamicTests(CypressTestResults testResults) {
    List<DynamicContainer> dynamicContainers = new ArrayList<>();
    List<CypressTestSuite> suites = testResults.getSuites();
    for (CypressTestSuite suite : suites) {
      createContainerFromSuite(dynamicContainers, suite);
    }
    return dynamicContainers;
  }

  private void createContainerFromSuite(
      List<DynamicContainer> dynamicContainers, CypressTestSuite suite) {
    List<DynamicTest> dynamicTests = new ArrayList<>();
    for (CypressTest test : suite.getTests()) {
      dynamicTests.add(
          DynamicTest.dynamicTest(
              test.getDescription(),
              () -> {
                if (!test.isSuccess()) {
                  log.error(test.getErrorMessage());
                  log.error(test.getStackTrace());
                }
                Assertions.assertThat(test.isSuccess()).isTrue();
              }));
    }
    dynamicContainers.add(DynamicContainer.dynamicContainer(suite.getTitle(), dynamicTests));
  }

  @TestConfiguration
  static class Configuration {

    @Bean
    @Primary
    public TemplateGenerator generators(Environment environment) {
      if (environment.matchesProfiles("maven-test")) {
        log.info("Using pre-compiled generator for tests");
        return new PreCompiledGenerator();
      }
      log.info("Using AsciiDoc generator for tests");
      return new AsciiDocGenerator();
    }
  }
}
