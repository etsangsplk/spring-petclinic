package org.springframework.samples.tracing;

import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import io.opentracing.contrib.spring.web.interceptor.*;
import io.opentracing.contrib.web.servlet.filter.TracingFilter;

@Configuration
public class TracingFilterConfiguration {
    private static Logger LOGGER = LogManager.getLogger();

    public FilterRegistrationBean tracingFilter() {
        Tracer tracer = GlobalTracer.get();
        TracingFilter tracingFilter = new TracingFilter(tracer);

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(tracingFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        filterRegistrationBean.setAsyncSupported(false);
        return filterRegistrationBean;
    }

}
