package org.owasp.wrongsecrets.asciidoc;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.checkerframework.checker.units.qual.A;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class PreCompiledGenerator implements TemplateGenerator {

    @Override
    public String generate(String name) throws IOException {
        var templateFile = name + ".html";

        try (var bos = new ByteArrayOutputStream()) {
            FileCopyUtils.copy(new ClassPathResource(templateFile).getInputStream(), bos);
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
