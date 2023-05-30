package org.owasp.wrongsecrets.asciidoc;

import java.io.IOException;

/** Template generator used for Asciidoc to HTML conversion. */
public interface TemplateGenerator {

  String generate(String name) throws IOException;
}
