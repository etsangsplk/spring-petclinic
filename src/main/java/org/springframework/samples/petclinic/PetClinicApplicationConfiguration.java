package org.springframework.samples.petclinic;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.tracing.TracerConfiguration;
import org.springframework.samples.tracing.TracingFilterConfiguration;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.tracing.TracingHandlerDecorator;

import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.contrib.spring.web.interceptor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.opentracing.contrib.web.servlet.filter.TracingFilter;
import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import io.opentracing.contrib.spring.web.interceptor.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.regex.Pattern;

@Configuration
public class PetClinicApplicationConfiguration implements WebMvcConfigurer, ServletContextListener  {
    private static Logger LOGGER = LogManager.getLogger();

    @Bean
    public Tracer tracer() {
        return (new TracerConfiguration()).getTracer(PetClinicApplication.SERVICE_NAME);
    }

    @Bean
    public List<HandlerInterceptorSpanDecorator> spanDecorators() {
        return Arrays.asList(new TracingHandlerDecorator(), HandlerInterceptorSpanDecorator.STANDARD_LOGS,
            HandlerInterceptorSpanDecorator.HANDLER_METHOD_OPERATION_NAME);
    }

    @Bean
    public FilterRegistrationBean filterRegistratioBean() {
        return (new TracingFilterConfiguration()).tracingFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TracingHandlerInterceptor(GlobalTracer.get(), spanDecorators()));
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(TracingFilter.SPAN_DECORATORS,
            Collections.singletonList(ServletFilterSpanDecorator.STANDARD_TAGS));
        sce.getServletContext().setAttribute(TracingFilter.SKIP_PATTERN, Pattern.compile("/health"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
