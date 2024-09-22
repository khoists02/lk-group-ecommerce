package com.lkgroup.ecommerce.services.user_service.api.auth;

import com.lkgroup.ecommerce.protobuf.userproto.AuthenticationProtos;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@RestController
public class CsrfController {
    @Autowired
    private CookieCsrfTokenRepository csrfTokenRepository;

    @PostMapping("/csrf")
    public AuthenticationProtos.CsrfResponse getCsrfToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie csrfCookie = WebUtils.getCookie(request, Http.XSRF_TOKEN);
        if(csrfCookie != null)
        {
            return AuthenticationProtos.CsrfResponse.newBuilder()
                    .setToken(csrfCookie.getValue())
                    .build();
        }

        Optional<String> csrfCookieHeader = response.getHeaders(Http.SET_COOKIE).stream().filter(c->c.contains(Http.XSRF_TOKEN)).findFirst();
        if(csrfCookieHeader.isEmpty())
        {
            csrfTokenRepository.saveToken(csrfTokenRepository.generateToken(request), request, response);
        }

        return AuthenticationProtos.CsrfResponse.newBuilder()
                .setToken(response.getHeaders(Http.SET_COOKIE).stream().filter(c->c.contains(Http.XSRF_TOKEN)).findFirst().get().split(";")[0].split("=")[1])
                .build();

    }
}
