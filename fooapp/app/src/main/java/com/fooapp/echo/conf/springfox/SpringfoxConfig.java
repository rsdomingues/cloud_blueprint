package com.fooapp.echo.conf.springfox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "io.swagger")
public class SpringfoxConfig {

    @Bean
    public Docket documentation() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fooapp.echo.gateway.http"))
            .paths(regex("/api/.*"))
            .build();
    }

    @Bean
    public UiConfiguration uiConfig() {
        return new UiConfiguration((String) null);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("The great Foo App")
            .version("1.0")
            .build();
    }

}
