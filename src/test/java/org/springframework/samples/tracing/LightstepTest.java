package org.springframework.samples.tracing;

import com.google.common.collect.ImmutableMap;
import com.lightstep.tracer.jre.JRETracer;
import com.lightstep.tracer.shared.Options;
import com.lightstep.tracer.shared.Status;
import io.opentracing.Tracer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LightstepTest {
    private static final Logger LOGGER = LogManager.getLogger();
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
        Status status = ((JRETracer) tracer).status();
        assertTrue(status.hasTag("lightstep.component_name"));
        assertTrue(status.hasTag("lightstep.guid"));
        assertEquals("test", status.getTag("lightstep.component_name"));
    }

}
