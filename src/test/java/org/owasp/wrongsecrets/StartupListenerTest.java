package org.owasp.wrongsecrets;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.SystemStubs.*;

@SpringJUnitConfig
public class StartupListenerTest {
    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;

    @Test
    public void testFailStartupWithMissingK8s_ENV_Var() throws Exception {
        AtomicInteger statusCode = new AtomicInteger();
        AtomicReference<String> text=new AtomicReference<>();
        new SystemProperties("nok8senv", "atall")
            .execute(()->{
                var ape = new ApplicationEnvironmentPreparedEvent(new DefaultBootstrapContext(), new SpringApplication(), new String[0], configurableApplicationContext.getEnvironment());
                var startupListener = new StartupListener();
                 text.set(tapSystemErrAndOut(() -> {
                     statusCode.set(catchSystemExit(() -> {
                         startupListener.onApplicationEvent(ape);
                     }));
                 }));

            });
        assertThat(statusCode.get()).isEqualTo(1);
        assertThat(text.get()).contains("ROR org.owasp.wrongsecrets.StartupListener - K8S_ENV does not contain one of the expected values: DOCKER, HEROKU_DOCKER, GCP, AWS, AZURE, VAULT, K8S.");
    }

    @Test
    public void testFailStartupWithWrongOrMissingVar() throws Exception {
        AtomicInteger statusCode = new AtomicInteger();
        AtomicReference<String> text=new AtomicReference<>();
        new SystemProperties("K8S_ENV", "blabla")
            .execute(()->{
                var ape = new ApplicationEnvironmentPreparedEvent(new DefaultBootstrapContext(), new SpringApplication(), new String[0], configurableApplicationContext.getEnvironment());
                var startupListener = new StartupListener();
                text.set(tapSystemErrAndOut(() -> {
                    statusCode.set(catchSystemExit(() -> {
                        startupListener.onApplicationEvent(ape);
                    }));
            }));
                });
        assertThat(statusCode.get()).isEqualTo(1);
        assertThat(text.get()).contains("ROR org.owasp.wrongsecrets.StartupListener - K8S_ENV does not contain one of the expected values: DOCKER, HEROKU_DOCKER, GCP, AWS, AZURE, VAULT, K8S.");
    }

    @Test
    public void testWithK8S_ENVset() throws Exception {
        new SystemProperties("K8S_ENV", "DOCKER")
            .execute(()->{
                var ape = new ApplicationEnvironmentPreparedEvent(new DefaultBootstrapContext(), new SpringApplication(), new String[0], configurableApplicationContext.getEnvironment());
                var startupListener = new StartupListener();
                startupListener.onApplicationEvent(ape);

            });
    }


}
