package org.springframework.samples.tracing;

import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.JaegerSpanContext;
import io.jaegertracing.spi.Reporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JaegerSpanReporter implements Reporter {
    private static Logger LOGGER = LogManager.getLogger();

    //@SuppressWarnings("checkstyle:LineLength")
    private static String spanLogMessageFmt = "Span finished traceID={} "
        .concat("spanID={} ")
        .concat("parentSpanID={} ")
        .concat("operation={} ")
        .concat("start={} ")
        .concat("duration={}");

    @Override
    public void report(JaegerSpan span) {
        if (span != null) {
            JaegerSpanContext spanContext = span.context();
            LOGGER.info(String.format(spanLogMessageFmt,
                spanContext.getTraceId(),
                spanContext.getSpanId(),
                spanContext.getParentId(),
                span.getOperationName(),
                span.getStart(),
                span.getDuration()));
        }
    }

    @Override
    public void close() {
        //nothing to do
    }

}
