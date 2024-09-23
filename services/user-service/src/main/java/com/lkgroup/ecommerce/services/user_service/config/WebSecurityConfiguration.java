package com.lkgroup.ecommerce.services.user_service.config;

import com.lkgroup.ecommerce.services.user_service.api.auth.UnauthenticatedHandler;
import com.lkgroup.ecommerce.services.user_service.api.auth.UnauthorisedHandler;
import com.lkgroup.ecommerce.services.user_service.api.filters.CookieAuthFilter;
import com.lkgroup.ecommerce.services.user_service.api.filters.CorsFilter;
import com.lkgroup.ecommerce.services.user_service.api.filters.FilterChainExceptionHandler;
import com.lkgroup.ecommerce.services.user_service.api.filters.LogContextFilter;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.lkgroup.ecommerce.services.user_service.api.auth.CookieCsrfTokenRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    private final AutowireCapableBeanFactory beanFactory;
    private final UnauthenticatedHandler unauthenticatedHandler;
    private final UnauthorisedHandler unauthorisedHandler;
    public final CookieCsrfTokenRepository csrfTokenRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public WebSecurityConfiguration(
            AutowireCapableBeanFactory beanFactory,
            UnauthenticatedHandler unauthenticatedHandler,
            UnauthorisedHandler unauthorisedHandler, CookieCsrfTokenRepository csrfTokenRepository) {
        this.beanFactory = beanFactory;
        this.unauthenticatedHandler = unauthenticatedHandler;
        this.unauthorisedHandler = unauthorisedHandler;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Bean
    public CorsFilter corsFilter() {
        return beanFactory.createBean(CorsFilter.class);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   CorsFilter corsFilter
        ) throws Exception {
        httpSecurity.addFilterAfter(beanFactory.createBean(LogContextFilter.class), CsrfFilter.class);
        httpSecurity.addFilterAfter(beanFactory.createBean(FilterChainExceptionHandler.class), LogContextFilter.class);
        httpSecurity.addFilterAfter(beanFactory.createBean(CookieAuthFilter.class), FilterChainExceptionHandler.class);
        httpSecurity.addFilterAfter(corsFilter, AuthorizationFilter.class);
        httpSecurity.csrf(x -> x.ignoringRequestMatchers("/csrf").csrfTokenRepository(csrfTokenRepository).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));
        // Need to disable the built in CORS as we have our own
        httpSecurity.cors(AbstractHttpConfigurer::disable);
//        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.exceptionHandling(x -> x.authenticationEntryPoint(unauthenticatedHandler).accessDeniedHandler(unauthorisedHandler));
        httpSecurity.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/auth/**", "/csrf")
                .permitAll()
                .anyRequest()
                .authenticated()
        );
        httpSecurity.sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }
}
