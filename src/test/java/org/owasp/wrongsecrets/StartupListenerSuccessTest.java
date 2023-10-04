package org.owasp.wrongsecrets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest(properties = {"spring.application.name=example", "K8S_ENV=DOCKER"})
class StartupListenerSuccessTest {

  @Autowired ConfigurableApplicationContext configurableApplicationContext;

  @Test
  void testWithK8S_ENVsetPropperly() {
    var ape =
        new ApplicationEnvironmentPreparedEvent(
            new DefaultBootstrapContext(),
            new SpringApplication(),
            new String[0],
            configurableApplicationContext.getEnvironment());
    var startupListener = new StartupListener();
    startupListener.onApplicationEvent(ape);
  }
}
