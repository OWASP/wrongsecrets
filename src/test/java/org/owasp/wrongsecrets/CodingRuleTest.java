package org.owasp.wrongsecrets;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.boot.test.web.server.LocalServerPort;

@AnalyzeClasses(packages = "org.owasp.wrongsecrets")
public class CodingRuleTest {
  private static final ArchCondition<JavaClass> USE_JUNIT =
      dependOnClassesThat(resideInAPackage("org.junit.jupiter.api.Assertions")).as("use JUnit");

  @ArchTest
  public static final ArchRule NO_CLASSES_SHOULD_USE_JUNIT_ASSERTIONS =
      noClasses().should(USE_JUNIT).because("we use AssertJ for assertions");

  @ArchTest
  public static final ArchRule NO_CLASSES_SHOULD_USE_LOCAL_SERVER_PORT =
      noFields()
          .that()
          .areNotDeclaredIn(CypressIntegrationTest.class)
          .should()
          .beAnnotatedWith(LocalServerPort.class)
          .because("we use AutoConfigureMockMvc for testing");
}
