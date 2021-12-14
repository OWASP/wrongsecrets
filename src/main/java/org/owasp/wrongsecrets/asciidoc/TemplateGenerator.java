package org.owasp.wrongsecrets.asciidoc;

import java.io.IOException;

public interface TemplateGenerator {

    String generate(String name) throws IOException;
}
