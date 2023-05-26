package org.owasp.wrongsecrets.asciidoc;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

/**
 * Thymeleaf resolver for AsciiDoc used in the lesson, can be used as follows inside a lesson file.
 * See <a href="https://github.com/OWASP/wrongsecrets#automatic-reload-during-development">The repo
 * readme</a> for more details on how to use it
 */
@Slf4j
public class AsciiDoctorTemplateResolver extends FileTemplateResolver {

  private static final String PREFIX = "doc:";
  private final TemplateGenerator generator;

  public AsciiDoctorTemplateResolver(TemplateGenerator generator) {
    this.generator = generator;
    setResolvablePatterns(Set.of(PREFIX + "*"));
  }

  @Override
  protected ITemplateResource computeTemplateResource(
      IEngineConfiguration configuration,
      String ownerTemplate,
      String template,
      String resourceName,
      String characterEncoding,
      Map<String, Object> templateResolutionAttributes) {
    var templateName = resourceName.substring(PREFIX.length());
    try {
      return new StringTemplateResource(generator.generate(computeResourceName(templateName)));
    } catch (IOException e) {
      return new StringTemplateResource("");
    }
  }

  private String computeResourceName(String resourceName) {
    return String.format("explanations/%s", resourceName.replace(".adoc", ""));
  }
}
