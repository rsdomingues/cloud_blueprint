package com.fooapp.echo.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "fooapp", ignoreUnknownFields = false)
public class AppProperties {

    private final Async async = new Async();

    public Async getAsync() {
        return async;
    }

    @Data
    public static class Async {
        private Integer corePoolSize;
    }
}