package org.springframework.samples.tracing;

import com.google.common.collect.ImmutableMap;
import com.lightstep.tracer.jre.JRETracer;
import com.lightstep.tracer.shared.Options;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LightstepTest {
    private static final Logger LOGGER = LogManager.getLogger();
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    @Test
    public void tracerInitializedWithServiceName() throws Exception {

        LOGGER.info("LightStep is enabled");
        ImmutableMap<String,String> config = ImmutableMap.of(
            "LIGHTSTEP_ENABLED",
            String.valueOf(Options.VERBOSITY_DEBUG),
            "LIGHTSTEP_ACCESSTOKEN",
            "testToken");

        Tracer tracer = Lightstep.registerTracer("test", config);
        assertThat(tracer, instanceOf(JRETracer.class));

    }
}
