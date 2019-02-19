package org.springframework.samples.petclinic;

import org.springframework.samples.tracing.TracerConfiguration;
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

import java.util.Arrays;
import java.util.List;

public class PetClinicApplicationConfiguration implements WebMvcConfigurer {
    private static Logger LOGGER = LogManager.getLogger();

    @Bean
    public Tracer tracer() {
        return (new TracerConfiguration()).getTracer(PetClinicApplication.SERVICE_NAME);
    }

    @Bean
    public List<HandlerInterceptorSpanDecorator> spanDecorators() {
        return Arrays.asList(new TracingHandlerDecorator());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new TracingHandlerInterceptor(GlobalTracer.get(), spanDecorators()));
        LOGGER.info("++++++++++++++++++++++++");
    }

}
