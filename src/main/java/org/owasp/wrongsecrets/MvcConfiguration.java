package org.owasp.wrongsecrets;

import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.owasp.wrongsecrets.asciidoc.AsciiDocGenerator;
import org.owasp.wrongsecrets.asciidoc.AsciiDoctorTemplateResolver;
import org.owasp.wrongsecrets.asciidoc.PreCompiledGenerator;
import org.owasp.wrongsecrets.asciidoc.TemplateGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Set;

@Configuration
@Slf4j
public class MvcConfiguration implements WebMvcConfigurer {

    private static final String UTF8 = "UTF-8";

    @Bean
    public ITemplateResolver springThymeleafTemplateResolver(ApplicationContext applicationContext) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setOrder(2);
        resolver.setCacheable(false);
        resolver.setCharacterEncoding(UTF8);
        resolver.setApplicationContext(applicationContext);
        return resolver;
    }

    @Bean
    public TemplateGenerator generator(@Value("${asciidoctor.enabled}") boolean asciiDoctorEnabled) {
        if (asciiDoctorEnabled) {
            return new AsciiDocGenerator();
        }
        return new PreCompiledGenerator();
    }

    @Bean
    public AsciiDoctorTemplateResolver asciiDoctorTemplateResolver(TemplateGenerator generator) {
        AsciiDoctorTemplateResolver resolver = new AsciiDoctorTemplateResolver(generator);
        resolver.setCacheable(false);
        resolver.setOrder(1);
        resolver.setCharacterEncoding(UTF8);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver springThymeleafTemplateResolver,
                                                        FileTemplateResolver asciiDoctorTemplateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setEnableSpringELCompiler(true);
        engine.addDialect(new LayoutDialect());
        engine.setTemplateResolvers(
            Set.of(asciiDoctorTemplateResolver, springThymeleafTemplateResolver));
        return engine;
    }
}
