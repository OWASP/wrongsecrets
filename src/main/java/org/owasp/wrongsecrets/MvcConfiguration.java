package org.owasp.wrongsecrets;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.owasp.wrongsecrets.asciidoc.AsciiDocGenerator;
import org.owasp.wrongsecrets.asciidoc.AsciiDoctorTemplateResolver;
import org.owasp.wrongsecrets.asciidoc.PreCompiledGenerator;
import org.owasp.wrongsecrets.asciidoc.TemplateGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/** Used to generate and return all the html in thymeleaf and convert asciidoc to html. */
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
  public ThymeleafViewResolver viewResolver(SpringTemplateEngine templateEngine) {
    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
    viewResolver.setTemplateEngine(templateEngine);
    viewResolver.setOrder(1);
    viewResolver.setViewNames(new String[] {".html", ".xhtml"});
    return viewResolver;
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
  public SpringTemplateEngine thymeleafTemplateEngine(
      ITemplateResolver springThymeleafTemplateResolver,
      FileTemplateResolver asciiDoctorTemplateResolver) {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setEnableSpringELCompiler(true);
    engine.addDialect(new LayoutDialect());
    engine.addDialect(new SpringSecurityDialect());
    engine.setTemplateResolvers(
        Set.of(asciiDoctorTemplateResolver, springThymeleafTemplateResolver));
    return engine;
  }
}
