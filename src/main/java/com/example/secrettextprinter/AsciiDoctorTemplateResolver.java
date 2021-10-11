package com.example.secrettextprinter;

import lombok.extern.slf4j.Slf4j;
import org.asciidoctor.Asciidoctor;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.asciidoctor.Asciidoctor.Factory.create;

/**
 * Thymeleaf resolver for AsciiDoc used in the lesson, can be used as follows inside a lesson file:
 * <p>
 * <code>
 * <div th:replace="doc:AccessControlMatrix_plan.adoc"></div>
 * </code>
 * Copied from WEbGoat
 */
@Slf4j
public class AsciiDoctorTemplateResolver extends FileTemplateResolver {

    private static final Asciidoctor asciidoctor = create();
    private static final String PREFIX = "doc:";

    public AsciiDoctorTemplateResolver(){
        setResolvablePatterns(Set.of(PREFIX + "*"));
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        var templateName = resourceName.substring(PREFIX.length());
        try (InputStream is = this.getClass().getResourceAsStream(computeResourceName(templateName))) {
            if (is == null) {
                log.warn("Resource name: {} not found, did you add the adoc file?", templateName);
                return new StringTemplateResource("");
            } else {

                StringWriter writer = new StringWriter();
                asciidoctor.convert(new InputStreamReader(is), writer, createAttributes());
                return new StringTemplateResource(writer.getBuffer().toString());
            }
        } catch (IOException e) {
            //no html yet
            return new StringTemplateResource("");
        }
    }

    /**
     * The resource name is for example HttpBasics_content1.adoc. This is always located in the following directory:
     * <code>plugin/HttpBasics/lessonPlans/en/HttpBasics_content1.adoc</code>
     */
    private String computeResourceName(String resourceName) {
        return String.format("/explanations/%s", resourceName);
    }

    private Map<String, Object> createAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("source-highlighter", "coderay");
        attributes.put("backend", "xhtml");
        attributes.put("icons", org.asciidoctor.Attributes.FONT_ICONS);

        Map<String, Object> options = new HashMap<>();
        options.put("attributes", attributes);

        return options;
    }
}
