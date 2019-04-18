package com.fooapp.echo.conf.web;

import com.fooapp.echo.conf.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.concurrent.TimeoutException;

@Configuration
@Slf4j
public class WebConfiguration extends WebMvcConfigurerAdapter {

    private final AppProperties appProperties;

    public WebConfiguration(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.registerDeferredResultInterceptors(
                new DeferredResultProcessingInterceptorAdapter() {
                    @Override
                    public <T> boolean handleTimeout(final NativeWebRequest request, final DeferredResult<T> result) {
                        log.warn("Async deferred result expired");
                        result.setErrorResult(new TimeoutException());
                        return false;
                    }
                });
    }

    @Bean
    public TaskExecutor asyncTaskExecutor() {
        final ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        final Integer corePoolSize = appProperties.getAsync().getCorePoolSize();
        if (corePoolSize != null) {
            log.info("Async task executor with core pool size is " + corePoolSize);
            pool.setCorePoolSize(corePoolSize);
        }
        pool.setThreadGroupName("ASYNC_EXECUTOR");
        return pool;
    }

}
