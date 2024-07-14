package org.owasp.wrongsecrets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

/**
 * Dynamically resolve a UI snippet for a specific challenge.
 *
 * <p>Thymeleaf will invoke this resolver based on the prefix and this implementation will resolve
 * the file in the resource directory.
 */
@Slf4j
public class ChallengeUiTemplateResolver extends FileTemplateResolver {

  private static final String PREFIX = "ui-snippet:";
  private ResourceLoader resourceLoader;
  private Map<String, byte[]> resources = new HashMap<>();

  public ChallengeUiTemplateResolver(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
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
    if (!StringUtils.hasText(templateName) || "null".equals(templateName)) {
      return new StringTemplateResource("<script></script>");
    }
    byte[] resource = resources.get(templateName);
    if (resource == null) {
      try {
        resource =
            resourceLoader
                .getResource("classpath:/" + templateName)
                .getInputStream()
                .readAllBytes();
      } catch (IOException e) {
        log.error("Unable to find resource {}", templateName, e);
        return new StringTemplateResource("");
      }
      resources.put(templateName, resource);
    }
    return new StringTemplateResource(new String(resource, StandardCharsets.UTF_8));
  }
}
