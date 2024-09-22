package com.lkgroup.ecommerce.services.user_service.api.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class LogContextFilter extends OncePerRequestFilter {
    public static final String USER_ID_KEY = "userId";
    public static final String SESSION_ID_KEY = "sessionId";
    private final String TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        try{

            String traceToken = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceToken);
            httpServletResponse.addHeader("X-TRACE-ID", traceToken);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }finally{
            MDC.clear();
        }
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
