package org.owasp.wrongsecrets;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class StartupListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent envEvent) {
            if (!StartupHelper.passedCorrectEnv(envEvent.getEnvironment().getProperty("K8S_ENV"))) {
                log.error("K8S_ENV does not contain one of the expected values: {}.", StartupHelper.envsToReadableString());
                throw new FailtoStartupException("K8S_ENV does not contain one of the expected values");
            }
        }
    }

    @UtilityClass
    private class StartupHelper {

        private boolean passedCorrectEnv(String k8sEnv) {
            try {
                RuntimeEnvironment.Environment.fromId(k8sEnv);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private String envsToReadableString() {
            return Arrays.stream(RuntimeEnvironment.Environment.values())
                .map(Enum::toString)
                .collect(Collectors.joining(", "));
        }
    }

}
