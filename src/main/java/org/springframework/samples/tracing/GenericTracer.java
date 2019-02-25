package org.springframework.samples.tracing;

import com.lightstep.tracer.jre.JRETracer;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.io.Closeable;

@Configuration
public class GenericTracer implements Closeable {
    private static final Logger LOGGER = LogManager.getLogger();

    private io.jaegertracing.Configuration config;

    /**
     * Construct a global opentracing tracer instance.
     * @param serviceName name of the microservice this tracer is responsible for.
     * @return Tracer returns a opentracing tracer.
     */
    public Tracer getTracer(String serviceName) {
        Assert.hasText(serviceName, "service name cannot be null");
        LOGGER.info("Initializing a tracer for service {}", serviceName);
        try {
            if (Lightstep.isEnabled()) {
                LOGGER.info("LightStep is enabled");
                ImmutableMap<String,String> config = Lightstep.settings();
                Tracer tracer = Lightstep.registerTracer(serviceName, config);
                GlobalTracer.register(tracer);
                return GlobalTracer.get();
            }
            if (Jaeger.isEnabled()) {
                LOGGER.info("Jaeger is enabled");
                // Jaeger API returns a "config", so have to register tracer inside registerTracer.
                ImmutableMap<String,String> config = Jaeger.settings();
                this.config = Jaeger.registerTracer(serviceName, config);
                return GlobalTracer.get();
            }
            GlobalTracer.register(NoopTracerFactory.create());
            return GlobalTracer.get();

        } catch (Exception ex) {
            LOGGER.warn("Tracer initialization failed with exception={}. returning NoopTracer.",
                ex.getMessage());
            return NoopTracerFactory.create();
        }
    }

    /**
     * Close a global opentracing tracer instance.
     */
    public void close() {
        if (Lightstep.isEnabled()) {
            io.opentracing.Tracer tracer = GlobalTracer.get();
            if (tracer instanceof JRETracer) {
                ((JRETracer) tracer).close();
            }
        }
        if (Jaeger.isEnabled()) {
            this.config.closeTracer();
        }
    }
}
