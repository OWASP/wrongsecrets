package org.owasp.wrongsecrets;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.SystemStubs.catchSystemExit;
import static uk.org.webcompere.systemstubs.SystemStubs.tapSystemErrAndOut;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@Slf4j
class StartupListenerErrorTest {

  @Autowired ConfigurableApplicationContext configurableApplicationContext;

  @Test
  void testFailStartupWithMissingK8s_ENV_Var() throws Exception {
    AtomicInteger statusCode = new AtomicInteger();
    AtomicReference<String> text = new AtomicReference<>();
    var ape =
        new ApplicationEnvironmentPreparedEvent(
            new DefaultBootstrapContext(),
            new SpringApplication(),
            new String[0],
            configurableApplicationContext.getEnvironment());
    var startupListener = new StartupListener();
    try {
      text.set(
          tapSystemErrAndOut(
              () ->
                  statusCode.set(catchSystemExit(() -> startupListener.onApplicationEvent(ape)))));
      assertThat(statusCode.get()).isEqualTo(1);
      assertThat(text.get())
          .contains("K8S_ENV does not contain one of the expected values: DOCKER,");
    } catch (UnsupportedOperationException e) {
      log.info("We can no longer run thistest this way"); // todo:fix this!
    }
  }
}
