package org.springframework.samples.tracing;

import com.lightstep.tracer.jre.JRETracer;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;

import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LightstepTest {
    @Test
    public void tracerInitializedWithServiceName() throws Exception {
        Tracer tracer = Lightstep.registerTracer("test");
        assertThat(tracer, instanceOf(JRETracer.class));

    }
}
