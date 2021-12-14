package org.owasp.wrongsecrets.asciidoc;

import org.asciidoctor.Asciidoctor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

import static org.asciidoctor.Asciidoctor.Factory.create;

public class AsciiDocGenerator implements TemplateGenerator {

    private static final Asciidoctor asciidoctor = create();

    @Override
    public String generate(String name) throws IOException {
        var templateFile = name + ".adoc";
        try (var is = new ClassPathResource(templateFile).getInputStream()) {
            var writer = new StringWriter();
            asciidoctor.convert(new InputStreamReader(is), writer, Map.of());
            return writer.toString();
        }
    }
}
