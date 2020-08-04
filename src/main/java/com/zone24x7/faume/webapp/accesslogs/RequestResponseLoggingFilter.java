package com.zone24x7.faume.webapp.accesslogs;

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

@Component
@Order(1)
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger("access");

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String correlationId = UUID.randomUUID().toString();

        LOGGER.info("[REQUEST] UUID={} METHOD={} PATH={} QSTRING={} ORIGIN={}",
                    correlationId,
                    httpRequest.getMethod(),
                    httpRequest.getServletPath(),
                    httpRequest.getQueryString(),
                    httpRequest.getRemoteAddr());
        MDC.put("correlationId", "abc");
        long startTime = System.currentTimeMillis();

        chain.doFilter(request, response);

        LOGGER.info("[RESPONSE] UUID={} METHOD={} PATH={} QSTRING={} ORIGIN={} STIME={}ms STATUS={}",
                    correlationId,
                    httpRequest.getMethod(),
                    httpRequest.getServletPath(),
                    httpRequest.getQueryString(),
                    httpRequest.getRemoteAddr(),
                    System.currentTimeMillis() - startTime,
                    httpResponse.getStatus());
    }
}
