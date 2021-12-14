package org.owasp.wrongsecrets.asciidoc;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PreCompiledGenerator implements TemplateGenerator {

    @Override
    public String generate(String name) throws IOException {
        var templateFile = ResourceUtils.getFile(name + ".html");

        try (var bos = new ByteArrayOutputStream()) {
            Files.copy(templateFile.toPath(), bos);
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
