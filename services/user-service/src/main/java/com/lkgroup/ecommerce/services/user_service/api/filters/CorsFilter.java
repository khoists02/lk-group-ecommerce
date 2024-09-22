package com.lkgroup.ecommerce.services.user_service.api.filters;

import com.lkgroup.ecommerce.utils.RequestUtils;
import com.lkgroup.ecommerce.services.user_service.api.auth.AuthenticatedUser;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CorsFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionHandlerResolver;

    private final String allowedDomain = "hcm.lkgroup.local";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return pathMatcher.match("/actuator/**", request.getRequestURI()) ||
                pathMatcher.match("/integrations/*/oauth/token/callback", request.getRequestURI())
                || pathMatcher.match("/auth/saml2/*/acs", request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "HEAD", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(List.of(
                "Content-Disposition",
                "X-Rate-Limit-Try-After-Seconds",
                "X-Rate-Limit-Remaining"
        ));

        Authentication authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
        String requestOrigin = RequestUtils.getCustomerOrigin();
        if (authenticatedUser == null && requestOrigin != null) {
            //Not logged in, use current request Origin
            logger.debug("Unable locate customer for Origin: {}. CORS will be rejected", requestOrigin);
        } else if (authenticatedUser instanceof AuthenticatedUser) {
            configuration.addAllowedOrigin(requestOrigin);
        }
        Boolean isValid = processRequest(configuration, request, response);
        if (isValid && !CorsUtils.isPreFlightRequest(request)) {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Set the CORS headers for a Request that may not have passed through the filter on its own
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void applyHeaders(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "HEAD", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");

        String requestOrigin = RequestUtils.getCustomerOrigin();
        if (requestOrigin != null) {
            configuration.addAllowedOrigin(requestOrigin);
        } else {
            logger.debug("Unable locate customer for Origin: {}. CORS will be rejected", requestOrigin);
        }
        processRequest(configuration, request, response);
    }


    public boolean processRequest(@Nullable CorsConfiguration config, HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {

        Collection<String> varyHeaders = response.getHeaders(HttpHeaders.VARY);
        if (!varyHeaders.contains(HttpHeaders.ORIGIN)) {
            response.addHeader(HttpHeaders.VARY, HttpHeaders.ORIGIN);
        }
        if (!varyHeaders.contains(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD)) {
            response.addHeader(HttpHeaders.VARY, HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        }
        if (!varyHeaders.contains(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS)) {
            response.addHeader(HttpHeaders.VARY, HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        }

        if (!CorsUtils.isCorsRequest(request)) {
            return true;
        }

        if (response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN) != null) {
            logger.trace("Skip: response already contains \"Access-Control-Allow-Origin\"");
            return true;
        }

        boolean preFlightRequest = CorsUtils.isPreFlightRequest(request);
        if (config == null) {
            if (preFlightRequest) {
                rejectRequest(new ServletServerHttpRequest(request), new ServletServerHttpResponse(response));
                return false;
            } else {
                return true;
            }
        }

        return handleInternal(new ServletServerHttpRequest(request), new ServletServerHttpResponse(response), config, preFlightRequest);
    }

    protected void rejectRequest(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        exceptionHandlerResolver.resolveException(request.getServletRequest(), response.getServletResponse(), null, UnauthorizedException.INVALID_CORS_REQUEST);
    }

    protected boolean handleInternal(ServletServerHttpRequest request, ServletServerHttpResponse response,
                                     CorsConfiguration config, boolean preFlightRequest) throws IOException {

        String requestOrigin = request.getHeaders().getOrigin();
        String allowOrigin = checkOrigin(config, requestOrigin);
        HttpHeaders responseHeaders = response.getHeaders();

        if (allowOrigin == null) {
            logger.debug("Reject: '{}' origin is not allowed", requestOrigin);
            rejectRequest(request, response);
            return false;
        }

        HttpMethod requestMethod = getMethodToUse(request, preFlightRequest);
        List<HttpMethod> allowMethods = checkMethods(config, requestMethod);
        if (allowMethods == null) {
            logger.debug("Reject: HTTP '{}' is not allowed", requestMethod);
            rejectRequest(request, response);
            return false;
        }

        List<String> requestHeaders = getHeadersToUse(request, preFlightRequest);
        List<String> allowHeaders = checkHeaders(config, requestHeaders);
        if (preFlightRequest && allowHeaders == null) {
            logger.debug("Reject: headers '{}' are not allowed", requestHeaders);
            rejectRequest(request, response);
            return false;
        }

        responseHeaders.setAccessControlAllowOrigin(allowOrigin);

        if (preFlightRequest) {
            responseHeaders.setAccessControlAllowMethods(allowMethods);
        }

        if (preFlightRequest && !allowHeaders.isEmpty()) {
            responseHeaders.setAccessControlAllowHeaders(allowHeaders);
        }

        if (!CollectionUtils.isEmpty(config.getExposedHeaders())) {
            responseHeaders.setAccessControlExposeHeaders(config.getExposedHeaders());
        }

        if (Boolean.TRUE.equals(config.getAllowCredentials())) {
            responseHeaders.setAccessControlAllowCredentials(true);
        }

        if (preFlightRequest && config.getMaxAge() != null) {
            responseHeaders.setAccessControlMaxAge(config.getMaxAge());
        }

        response.flush();
        return true;
    }

    /**
     * Check the origin and determine the origin for the response. The default
     * implementation simply delegates to
     * {@link org.springframework.web.cors.CorsConfiguration#checkOrigin(String)}.
     */
    @Nullable
    protected String checkOrigin(CorsConfiguration config, @Nullable String requestOrigin) {
        return config.checkOrigin(requestOrigin);
    }

    /**
     * Check the HTTP method and determine the methods for the response of a
     * pre-flight request. The default implementation simply delegates to
     * {@link org.springframework.web.cors.CorsConfiguration#checkHttpMethod(HttpMethod)}.
     */
    @Nullable
    protected List<HttpMethod> checkMethods(CorsConfiguration config, @Nullable HttpMethod requestMethod) {
        return config.checkHttpMethod(requestMethod);
    }

    @Nullable
    private HttpMethod getMethodToUse(ServerHttpRequest request, boolean isPreFlight) {
        return (isPreFlight ? request.getHeaders().getAccessControlRequestMethod() : request.getMethod());
    }

    /**
     * Check the headers and determine the headers for the response of a
     * pre-flight request. The default implementation simply delegates to
     * {@link org.springframework.web.cors.CorsConfiguration#checkOrigin(String)}.
     */
    @Nullable
    protected List<String> checkHeaders(CorsConfiguration config, List<String> requestHeaders) {
        return config.checkHeaders(requestHeaders);
    }

    private List<String> getHeadersToUse(ServerHttpRequest request, boolean isPreFlight) {
        HttpHeaders headers = request.getHeaders();
        return (isPreFlight ? headers.getAccessControlRequestHeaders() : new ArrayList<>(headers.keySet()));
    }

}
