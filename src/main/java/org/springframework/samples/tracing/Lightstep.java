package org.springframework.samples.tracing;

import com.lightstep.tracer.jre.JRETracer;
import com.lightstep.tracer.shared.Options;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.String;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;


class Lightstep {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final boolean LIGHTSTEP_ENABLED = false;
    private static String  LIGHTSTEP_ACCESSTOKEN = "";

    private static final String LIGHTSTEP_ENABLED_ENVVAR = "LIGHTSTEP_ENABLED";
    private static final String LIGHTSTEP_ACCESSTOKEN_ENVVAR = "LIGHTSTEP_ACCESSTOKEN";
    private static final String LIGHTSTEP_VERBOSE_ENVVAR = "LIGHTSTEP_VERBOSE";

    // For java lightstep library defaults to collector-grpc.lightstep.com
    private static final String LIGHTSTEP_COLLECTORHOST_DEFAULT = "collector.lightstep.com";
    private static final int LIGHTSTEP_COLLECTORPORT_DEFAULT = 443;
    private static final String LIGHTSTEP_COLLECTORPROTOCOL_DEFAUlT = "https";

    /**
     * Construct a global Lighstep tracer instance.
     * @param serviceName name of the microservice this tracer is responsible for.
     */
    public static Tracer registerTracer(String serviceName, ImmutableMap<String, String> config) {
        try {
            LOGGER.info("Initializing Lightstep tracer with service name {}.", serviceName);
            int verbosity = Integer.parseInt(config.get(LIGHTSTEP_ENABLED_ENVVAR));
            String accesstoken = config.get(LIGHTSTEP_ACCESSTOKEN_ENVVAR);

            // TODO unable to use http to our http collector.
            Options options = new Options.OptionsBuilder()
                .withAccessToken(accesstoken)
                .withComponentName(serviceName)
                .withVerbosity(verbosity)
                .withMetaEventLogging(true)
                .build();
            Tracer tracer = new JRETracer(options);
            LOGGER.info("Tracer initialized with service name {}.", serviceName);
            return tracer;

        } catch (NoSuchElementException
            | MalformedURLException ex) {
            LOGGER.warn("Tracer initialization failed with exception={}. returning NoopTracer.",
                ex.getMessage());
            return NoopTracerFactory.create();
        }
    }

    /**
     * Releases all resources held by a LightStep tracer.
     */
    public void close() {
        Tracer tracer = GlobalTracer.get();
        if (tracer instanceof JRETracer) {
            ((JRETracer) tracer).close();
        }
    }

    /**
     * Releases all resources held by a LightStep tracer.
     * @return returns a boolean to indicate if lighttep is enabled.
     */
    public static boolean isEnabled() {
        return ConfigUtil.getOptionalBoolean(LIGHTSTEP_ENABLED_ENVVAR,
            LIGHTSTEP_ENABLED);
    }

    public static ImmutableMap<String, String> settings() {
        String verbosity = ConfigUtil.getOptionalEnv(LIGHTSTEP_VERBOSE_ENVVAR,
            String.valueOf(Options.VERBOSITY_INFO));
        String accesstoken = ConfigUtil.getRequiredEnv(LIGHTSTEP_ACCESSTOKEN_ENVVAR);
        return ImmutableMap.of(
            LIGHTSTEP_ENABLED_ENVVAR,
            verbosity,
            LIGHTSTEP_ACCESSTOKEN_ENVVAR,
            accesstoken);

    }
}
