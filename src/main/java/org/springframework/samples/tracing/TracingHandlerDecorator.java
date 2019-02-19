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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TracingHandlerDecorator implements HandlerInterceptorSpanDecorator {
    private static Logger LOGGER = LogManager.getLogger();

    @Override
    public void onPreHandle(HttpServletRequest httpServletRequest,
                            Object handler,
                            Span span) {
        String metaData = URLUtils.methodName(handler);
        if (metaData != null) {
            span.setOperationName(metaData);
            String requestId = URLUtils.getRequestId(httpServletRequest);
            String tenant = URLUtils.getTenantId(httpServletRequest);
            span.setTag("requestId", requestId);
            span.setTag("tenant", tenant);
        }
    }

    @Override
    public void onAfterCompletion(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse,
                                  Object handler,
                                  Exception ex,
                                  Span span) {}

    @Override
    public void onAfterConcurrentHandlingStarted(HttpServletRequest httpServletRequest,
                                                 HttpServletResponse httpServletResponse,
                                                 Object handler,
                                                 Span span) {}


    static class URLUtils {

        static Pattern PATH_PATTERN = Pattern.compile("\\/?(?<tenantId>.*?)\\/streams\\/v.*?\\/.*");

        /**
         * Extract tenant id from the request
         *
         * @param request http servelet request
         * @return tenant id, or null if tenant id couldn't be extracted
         */
        static String getTenantId(HttpServletRequest request) {
            String uri = request.getRequestURI();
            Matcher matcher = PATH_PATTERN.matcher(uri);
            if (matcher.find()) {
                return matcher.group("tenantId");
            }
            return "";
        }

        /**
         * Extract request id from the request
         *
         * @param request http servelet request
         * @return tenant id, or null if tenant id couldn't be extracted
         */
        static String getRequestId(HttpServletRequest request) {
            return Optional
                .ofNullable(request.getHeader("X-Request-Id"))
                .orElseGet(() -> ObjectId.get().toString());
        }

        /**
         * Extract method name from handler for span name.
         *
         * @param model the request URI
         * @return tenant id, or null if tenant id couldn't be extracted
         */
        static String methodName(Object handler) {
            return handler instanceof HandlerMethod
                ? ((HandlerMethod) handler).getMethod().getName() : null;
        }

    }
}
