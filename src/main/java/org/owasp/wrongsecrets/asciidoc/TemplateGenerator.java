package org.owasp.wrongsecrets.asciidoc;

import java.io.IOException;

/**
 * Template generagor used for Asciidoc to HTMLK conversion
 */
public interface TemplateGenerator {

    String generate(String name) throws IOException;
}
