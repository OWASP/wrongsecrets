package org.owasp.wrongsecrets.definitions;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.asciidoc.TemplateGenerator;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.docker.Challenge8;
import org.owasp.wrongsecrets.definitions.Sources.TextWithFileLocation;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
@Slf4j
@EnableConfigurationProperties(ChallengeDefinitionsConfiguration.class)
public class ChallengeConfig {

  @ConfigurationPropertiesBinding
  @Bean
  public TextWithFileLocationConverter textConverter(TemplateGenerator templateGenerator) {
    return new TextWithFileLocationConverter(templateGenerator);
  }

  @ConfigurationPropertiesBinding
  @Bean
  public StringToChallengeNameConverter nameConverter() {
    return new StringToChallengeNameConverter();
  }

  private record TextWithFileLocationConverter(
      TemplateGenerator templateGenerator, Map<String, Supplier<String>> cache)
      implements Converter<String, TextWithFileLocation> {

    public TextWithFileLocationConverter(TemplateGenerator templateGenerator) {
      this(templateGenerator, new ConcurrentHashMap<>());
    }

    @Override
    public TextWithFileLocation convert(String source) {
      Supplier<String> supplier = cache.computeIfAbsent(source, this::read);
      return new TextWithFileLocation(source, supplier);
    }

    private Supplier<String> read(String name) {
      return () -> {
        try {
          return templateGenerator.generate(FilenameUtils.removeExtension(name));
        } catch (IOException e) {
          log.error("Failed to load template: {}", name);
          return "";
        }
      };
    }
  }

  private record StringToChallengeNameConverter() implements Converter<String, ChallengeName> {
    @Override
    public ChallengeName convert(String name) {
      return new ChallengeName(name, name.strip().replace(" ", "-").toLowerCase());
    }
  }

  @Bean
  public Challenges challenges(
      ChallengeDefinitionsConfiguration challengeDefinitions, List<Challenge> challenges) {
    log.info(
        "Loaded {} definitions and {} challenges",
        challengeDefinitions.challenges().size(),
        challenges.size());

    challenges.stream()
        .filter(challenge -> challenge instanceof Challenge8)
        .findFirst()
        .ifPresent(Challenge::spoiler); // Using ifPresent is safer than .get()

    return new Challenges(challengeDefinitions, challenges);
  }
}
