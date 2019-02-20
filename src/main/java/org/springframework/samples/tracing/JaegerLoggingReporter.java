package org.springframework.samples.tracing;

import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.spi.Reporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class JaegerLoggingReporter implements Reporter {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void report(JaegerSpan span) {
        LOGGER.info("Span reported: {}", span);
    }

    @Override
    public void close() {
        // nothing to do
    }
}
