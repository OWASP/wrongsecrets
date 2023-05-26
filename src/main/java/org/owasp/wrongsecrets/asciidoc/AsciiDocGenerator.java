package org.owasp.wrongsecrets.asciidoc;

import static org.asciidoctor.Asciidoctor.Factory.create;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.springframework.core.io.ClassPathResource;

/**
 * Used for generating HTML out of asciidoc. Used for all Challenges' challenge texts, tips, and
 * explanations.
 */
public class AsciiDocGenerator implements TemplateGenerator {

  private static final Asciidoctor asciidoctor = create();

  @Override
  public String generate(String name) throws IOException {
    var templateFile = name + ".adoc";
    try (var is = new ClassPathResource(templateFile).getInputStream()) {
      var writer = new StringWriter();
      asciidoctor.convert(
          new InputStreamReader(is, StandardCharsets.UTF_8), writer, Options.builder().build());
      return writer.toString();
    }
  }
}
