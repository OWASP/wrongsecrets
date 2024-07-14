package org.owasp.wrongsecrets.definitions;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Represents the configuration part of the application. It consists of the global configuration and
 * the challenges, which we call {@link ChallengeDefinition}.
 *
 * <p>The complete configuration is read from a yaml file and it uses {@link
 * ConfigurationProperties}. This way we get full support for YAML anchors and references.
 */
@ConfigurationProperties(prefix = "configurations")
public record ChallengeDefinitionsConfiguration(
    List<Difficulty> difficulties,
    List<Technology> technologies,
    List<Environment> environments,
    List<ChallengeDefinition> challenges) {}
