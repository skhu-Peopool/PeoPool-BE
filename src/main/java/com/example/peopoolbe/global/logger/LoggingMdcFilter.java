package com.example.peopoolbe.global.logger;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingMdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest httpRequest) {
                String ip = httpRequest.getRemoteAddr();

                String userAgent = httpRequest.getHeader("User-Agent");

                MDC.put("clientIp", ip);
                MDC.put("userAgent", userAgent);
            }

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
