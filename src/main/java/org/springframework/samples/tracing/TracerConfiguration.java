package org.springframework.samples.tracing;

import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
public class TracerConfiguration {
    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean LIGHTSTEP_ENABLED = false;
    private static boolean JAEGER_ENABLED = false;

    private io.jaegertracing.Configuration config;

    /**
     * Construct a global opentrcing tracer instance.
     * @param serviceName name of the microservice this tracer is responsible for.
     * @return Tracer returns a opentracing tracer.
     */
    public Tracer getTracer(String serviceName) {
        Assert.hasText(serviceName, "service name cannot be null");
        LOGGER.info("Initializing a tracer for service {}", serviceName);
        try {
            if (Lightstep.isEnabled() == true) {
                LOGGER.info("LightStep is enabled");
                Tracer tracer = Lightstep.registerTracer(serviceName);
                GlobalTracer.register(tracer);
                return GlobalTracer.get();
            }
            if (Jaeger.isEnabled() == true) {
                LOGGER.info("Jaeger is enabled");
                // Jaeger API returns a "config", so have to register tracer inside registerTracer.
                this.config = Jaeger.registerTracer(serviceName);
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
     * Close a global opentrcing tracer instance.
     */
    public void close() {
        if (Lightstep.isEnabled() == true) {
            io.opentracing.Tracer tracer = GlobalTracer.get();
            if (tracer instanceof com.lightstep.tracer.jre.JRETracer) {
                ((com.lightstep.tracer.jre.JRETracer) tracer).close();
            }
        }
        if (Jaeger.isEnabled() == true) {
            this.config.closeTracer();
        }
    }
}
