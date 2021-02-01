package it.unipv.ingsw.l20PollingSystem.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/login").setViewName("sign-in");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/CSS/**")) {
            registry.addResourceHandler("/CSS/**").addResourceLocations("classpath:/CSS/");
        }
        if (!registry.hasMappingForPattern("/JS/**")) {
            registry.addResourceHandler("/JS/**").addResourceLocations("classpath:/JS/");
        }
        if (!registry.hasMappingForPattern("/img/**")) {
            registry.addResourceHandler("/img/**").addResourceLocations("classpath:/img/");
        }
    }
}
