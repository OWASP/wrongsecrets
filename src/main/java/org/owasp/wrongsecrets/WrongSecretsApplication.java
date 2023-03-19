package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.ChallengesController;
import org.owasp.wrongsecrets.challenges.kubernetes.Vaultpassword;
import org.owasp.wrongsecrets.oauth.TokenController;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@SpringBootApplication
@EnableConfigurationProperties(Vaultpassword.class)
public class WrongSecretsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WrongSecretsApplication.class, args);
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public InMemoryScoreCard scoreCard() {
        return new InMemoryScoreCard(28);
    }

    static {
        SpringDocUtils.getConfig().addRestControllers(AboutController.class, ChallengesController.class, IndexController.class, StatsController.class, TokenController.class);
    }

}
