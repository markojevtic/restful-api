package com.github.markojevtic.restfulapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Set;

@Configuration
@EnableSwagger2
public class BasicConfiguration {

    @Bean
    @Primary
    public ConversionService dtoConverters(Set<Converter<?, ?>> converters ) {
        final DefaultConversionService defaultConversionService = new DefaultConversionService();
        converters.forEach(defaultConversionService::addConverter);
        return defaultConversionService;
    }

    @Bean
    public Docket api() {
        return new Docket( DocumentationType.SWAGGER_2 )
                .select().apis( RequestHandlerSelectors.any() )
                .paths( PathSelectors.any() )
                .build();
    }
}
