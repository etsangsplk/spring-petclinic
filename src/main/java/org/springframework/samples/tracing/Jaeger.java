package org.springframework.samples.tracing;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.reporters.CompositeReporter;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.spi.Sender;
import io.jaegertracing.thrift.internal.senders.UdpSender;
import io.opentracing.util.GlobalTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;

class Jaeger {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final boolean JAEGER_ENABLED = false;
    private static String JAEGER_AGENT_HOST = "localhost";
    private static String JAEGER_AGENT_PORT = "6831";

    private static final String JAEGER_ENABLED_ENVVAR = "JAEGER_ENABLED";
    private static final String JAEGER_AGENT_HOST_ENVVAR = "JAEGER_AGENT_HOST";
    private static final String JAEGER_AGENT_PORT_ENVVAR = "JAEGER_AGENT_PORT";

    /**
     * Construct a global Jaeger tracer instance.
     * @param serviceName name of the microservice this tracer is responsible for.
     * @return a Jaeger Trace configuration that caller needs to close tracer.
     */
    public static Configuration registerTracer(String serviceName) {
        try {
            LOGGER.info("Initializing Jaeger tracer with service name {}.", serviceName);
            Configuration config = Configuration.fromEnv(serviceName);
            JaegerTracer.Builder tracerBuilder = config.getTracerBuilder();
            JaegerLoggingReporter loggingReporter = new JaegerLoggingReporter();
            JaegerSpanReporter spanReporter = new JaegerSpanReporter();
            Sender sender = senderFromEnv();
            Reporter remoteReporter = new RemoteReporter.Builder()
                .withSender(sender)
                .build();
            CompositeReporter compositeReporter = new CompositeReporter(
                loggingReporter,
                spanReporter,
                remoteReporter);
            JaegerTracer tracer = tracerBuilder
                .withSampler(new ConstSampler(true)) // Sending all spans for now.
                .withReporter(compositeReporter)
                .build();
            GlobalTracer.register(tracer);
            return config;
        } catch (NoSuchElementException ex) {
            LOGGER.warn("Tracer initialization failed with exception={}. returning NoopTracer.",
                ex.getMessage());

            return null;
        }
    }

    /**
     * Returns a trace sender from environment settings.
     * Currently only UdpSender is returned, HttpSender may be added in future.
     */
    private static Sender senderFromEnv() {
        String agentHost = ConfigUtil.getRequiredEnv(JAEGER_AGENT_HOST_ENVVAR);
        int agentPort = ConfigUtil.getRequiredInt(JAEGER_AGENT_PORT_ENVVAR);

        return new UdpSender(
            stringOrDefault(agentHost, UdpSender.DEFAULT_AGENT_UDP_HOST),
            numberOrDefault(agentPort, UdpSender.DEFAULT_AGENT_UDP_COMPACT_PORT).intValue(),
            0 /* max packet size */);
    }


    /**
     * Releases all resources held by a LightStep tracer.
     * @return returns a boolean to indicate if lightstep is enabled.
     */
    public static boolean isEnabled() {
        return ConfigUtil.getOptionalBoolean(JAEGER_ENABLED_ENVVAR,
            JAEGER_ENABLED);
    }

    private static String stringOrDefault(String value, String defaultValue) {
        return value != null && value.length() > 0 ? value : defaultValue;
    }

    private static Number numberOrDefault(Number value, Number defaultValue) {
        return value != null ? value : defaultValue;
    }
}
