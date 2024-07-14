package org.owasp.wrongsecrets.definitions;

import java.io.IOException;
import java.util.List;
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

  /**
   * Define a converter for {@link TextWithFileLocation} which reads the source location from the
   * configuration and reads the file contents. This way we only have to read the files once.
   *
   * @param templateGenerator the template generator
   * @return {@link StringToChallengeNameConverter}
   */
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

  private record TextWithFileLocationConverter(TemplateGenerator templateGenerator)
      implements Converter<String, TextWithFileLocation> {

    @Override
    public TextWithFileLocation convert(String source) {
      return new TextWithFileLocation(source, read(source));
    }

    private Supplier<String> read(String name) {
      return () -> {
        try {
          return templateGenerator.generate(FilenameUtils.removeExtension(name));
        } catch (IOException e) {
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
        .get()
        .spoiler(); // need early init to log the secret for debugging ;-).
    return new Challenges(challengeDefinitions, challenges);
  }
}
