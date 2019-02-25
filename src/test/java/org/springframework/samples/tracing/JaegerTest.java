package org.springframework.samples.tracing;

import com.google.common.collect.ImmutableMap;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.noop.NoopTracer;

import io.opentracing.util.GlobalTracer;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.String;

public class JaegerTest {

    @Test
    public void tracerReturnsNoopTracerOnException() throws Exception {
        ImmutableMap<String,String> settings = ImmutableMap.of(
            "JAEGER_AGENT_HOST",
            "localhost",
            "JAEGER_AGENT_PORT",
            "shouldthrow");
        String testName = "JaegerNoopTracer";
        Configuration config = Jaeger.registerTracer(testName, settings);
        assertNull("Jaeger Tracer should be null", config);
    }

    @Test
    public void tracerInitialized() throws Exception {
        ImmutableMap<String,String> settings = ImmutableMap.of(
            "JAEGER_AGENT_HOST",
            "localhost",
            "JAEGER_AGENT_PORT",
            "8080");
        String testName = "JaegerInitialized";
        Configuration config = Jaeger.registerTracer(testName, settings);
        Tracer tracer = GlobalTracer.get();

        JaegerTracer jTracer = config.getTracer();
        JaegerSpan jSpan = jTracer.buildSpan(testName).start();

        // Spot check to see if tracer is properly created.
        assertNotNull("Jaeger Tracer should not be null", jTracer);
        assertThat("Tracer should be of type Jaeger", jTracer, instanceOf(JaegerTracer.class));
        assertEquals("Tracer should have the service name",testName, jTracer.getServiceName());
        assertTrue("Global Tracer should be regisered", GlobalTracer.isRegistered());
        assertNotNull("Span should not be null", jSpan);
        assertEquals("Span operation name should be testspan",testName, jSpan.getOperationName());
    }
}
