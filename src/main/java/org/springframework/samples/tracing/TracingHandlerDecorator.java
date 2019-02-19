package org.springframework.samples.tracing;

import io.opentracing.Span;
import io.opentracing.contrib.spring.web.interceptor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class TracingHandlerDecorator implements HandlerInterceptorSpanDecorator {
    private static Logger LOGGER = LogManager.getLogger();

    @Override
    public void onPreHandle(HttpServletRequest httpServletRequest,
                            Object handler,
                            Span span) {
        String metaData = HandlerUtils.methodName(handler);
        LOGGER.info("*************tracinghandledecorator***********");
        LOGGER.info("*******Global tracer*********** ={}", io.opentracing.util.GlobalTracer.get());
        if (metaData != null) {
            span.setOperationName(metaData);
            //SplunkUri uri = new SplunkUri(httpServletRequest.getRequestURI());
            //String tenantId = uri.getTenant();
            String requestId = getRequestId(httpServletRequest);
            span.setTag("requestId", requestId);
            //span.setTag("tenant", tenantId);
        }
    }

    @Override
    public void onAfterCompletion(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse,
                                  Object handler,
                                  Exception ex,
                                  Span span) {
        LOGGER.info("*************tracinghandledecorator ONAFTERCOMPLETION ***********");
    }

    @Override
    public void onAfterConcurrentHandlingStarted(HttpServletRequest httpServletRequest,
                                                 HttpServletResponse httpServletResponse,
                                                 Object handler,
                                                 Span span) {
        LOGGER.info("*************tracinghandledecorator AFTERCONCURRENTHANDLEING  STARTED***********");
    }

    private String getRequestId(HttpServletRequest request) {
        return Optional
            .ofNullable(request.getHeader("X-Request-Id"))
            .orElseGet(() -> ObjectId.get().toString());
    }

    /**
     * Helper class for deriving tags/logs from handler object.
     */
    static class HandlerUtils {
        private HandlerUtils() {}

        /**
         * Class name of a handler serving request.
         */
        public static final String HANDLER_CLASS_NAME = "handler.class_simple_name";
        /**
         * Method name of handler serving request.
         */
        public static final String HANDLER_METHOD_NAME = "handler.method_name";
        /**
         * Spring handler object.
         */
        public static final String HANDLER = "handler";

        public static String methodName(Object handler) {
            return handler instanceof HandlerMethod
                ? ((HandlerMethod) handler).getMethod().getName() : null;
        }
    }


}
