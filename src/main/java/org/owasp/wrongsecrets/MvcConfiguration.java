package org.owasp.wrongsecrets;

import java.util.List;
import java.util.Locale;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
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

  /** Supported locales for the language toggle. */
  public static final List<Locale> SUPPORTED_LOCALES =
      List.of(
          Locale.ENGLISH,
          Locale.of("nl"),
          Locale.GERMAN,
          Locale.FRENCH,
          Locale.of("es"),
          Locale.of("uk"));

  private static final String UTF8 = "UTF-8";

  /** Session-based locale resolver so the selected language persists across requests. */
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(Locale.ENGLISH);
    return slr;
  }

  /** Interceptor that reads the {@code lang} request parameter and updates the session locale. */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName("lang");
    return lci;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }

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

  /** Loads the html for the complete lesson, see lesson_content.html */
  @Bean
  public ChallengeUiTemplateResolver uiTemplateResolver(ResourceLoader resourceLoader) {
    ChallengeUiTemplateResolver resolver = new ChallengeUiTemplateResolver(resourceLoader);
    resolver.setOrder(0);
    resolver.setCacheable(false);
    resolver.setCharacterEncoding(UTF8);
    return resolver;
  }

  @Bean
  public SpringTemplateEngine thymeleafTemplateEngine(
      ITemplateResolver springThymeleafTemplateResolver,
      ITemplateResolver uiTemplateResolver,
      FileTemplateResolver asciiDoctorTemplateResolver) {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setEnableSpringELCompiler(true);
    engine.addDialect(new LayoutDialect());
    engine.addDialect(new SpringSecurityDialect());
    engine.setTemplateResolvers(
        Set.of(asciiDoctorTemplateResolver, uiTemplateResolver, springThymeleafTemplateResolver));
    return engine;
  }
}
