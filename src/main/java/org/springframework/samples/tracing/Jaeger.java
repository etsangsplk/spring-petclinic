package org.springframework.samples.tracing;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
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
     * @param setting immutatble map of configuration settings.from environment variables.
     * @return a Jaeger Trace configuration that caller needs to close tracer.
     */
    public static Configuration registerTracer(String serviceName,
                                               ImmutableMap<String, String> setting) {
        try {
            LOGGER.info("Initializing Jaeger tracer with service name {}.", serviceName);
            Configuration config = Configuration.fromEnv(serviceName);
            JaegerTracer.Builder tracerBuilder = config.getTracerBuilder();
            JaegerLoggingReporter loggingReporter = new JaegerLoggingReporter();
            JaegerSpanReporter spanReporter = new JaegerSpanReporter();
            Sender sender = senderFromEnv(setting);
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
        } catch (Exception ex) {
            LOGGER.warn("Tracer initialization failed with exception={}. returning NoopTracer.",
                ex.getMessage());
            return null;
        }
    }

    /**
     * Returns a trace sender from environment settings.
     * Currently only UdpSender is returned, HttpSender may be added in future.
     */
    private static Sender senderFromEnv(ImmutableMap<String, String> config) {

        String agentHost = config.get(JAEGER_AGENT_HOST_ENVVAR);
        int agentPort = Integer.parseInt(config.get(JAEGER_AGENT_PORT_ENVVAR));

        return new UdpSender(
            agentHost,
            agentPort,
            0 /* max packet size */);
    }


    /**
     * Releases all resources held by a LightStep tracer.
     * @return returns a boolean to indicate if lighttep is enabled.
     */
    public static boolean isEnabled() {
        return ConfigUtil.getOptionalBoolean(JAEGER_ENABLED_ENVVAR,
            JAEGER_ENABLED);
    }

    /**
     * Returns an immutable map of settings to configure Jaeger tracer.
     * @return returns a ImmutableMap of Jaeger configuration settings.
     */
    public static ImmutableMap<String, String> settings() {
        String host = ConfigUtil.getOptionalEnv(JAEGER_AGENT_HOST_ENVVAR,
            JAEGER_AGENT_HOST);
        String port = ConfigUtil.getOptionalEnv(JAEGER_AGENT_PORT_ENVVAR,
            JAEGER_AGENT_PORT);
        return ImmutableMap.of(
            JAEGER_AGENT_HOST_ENVVAR,
            host,
            JAEGER_AGENT_PORT_ENVVAR,
            port);
    }


    private static String stringOrDefault(String value, String defaultValue) {
        if (Strings.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    private static int numberOrDefault(int value, int defaultValue) {
        return (value < 1) ? value : defaultValue;
    }
}
