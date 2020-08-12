package com.zone24x7.faume.webapp.accesslogs;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Class to represent the request response logging filter.
 */
@Component
@Order(1)
public class RequestResponseLoggingFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger("access");

    private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_ATTRIBUTE_NAME = "correlationId";

    /**
     * Method to apply filtering.
     *
     * @param request the request
     * @param response the response
     * @param chain the filter chain
     * @throws IOException if an IO error occurs
     * @throws ServletException if an servlet error occurs
     */
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        final String correlationId = getCorrelationIdFromHeader(httpRequest);

        LOGGER.info("[CorrelationId: {}] METHOD={} PATH={} QSTRING={} ORIGIN={}",
                    correlationId,
                    httpRequest.getMethod(),
                    httpRequest.getServletPath(),
                    httpRequest.getQueryString(),
                    httpRequest.getRemoteAddr());
        MDC.put(CORRELATION_ID_MDC_ATTRIBUTE_NAME, correlationId);
        long startTime = System.currentTimeMillis();

        chain.doFilter(request, response);

        LOGGER.info("[CorrelationId: {}] METHOD={} PATH={} QSTRING={} ORIGIN={} STIME={}ms STATUS={}",
                    correlationId,
                    httpRequest.getMethod(),
                    httpRequest.getServletPath(),
                    httpRequest.getQueryString(),
                    httpRequest.getRemoteAddr(),
                    System.currentTimeMillis() - startTime,
                    httpResponse.getStatus());

        MDC.remove(CORRELATION_ID_MDC_ATTRIBUTE_NAME);
    }

    /**
     * Method to get the correlation id from the header and generate if not available.
     *
     * @param request the request
     * @return the correlation id
     */
    private String getCorrelationIdFromHeader(final HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);

        if (StringUtils.isEmpty(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        return correlationId;
    }
}
