package com.example.secrettextprinter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
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
    public AsciiDoctorTemplateResolver asciiDoctorTemplateResolver(Language language) {
        AsciiDoctorTemplateResolver resolver = new AsciiDoctorTemplateResolver(language);
        resolver.setCacheable(false);
        resolver.setOrder(1);
        resolver.setCharacterEncoding(UTF8);
        return resolver;
    }
}
