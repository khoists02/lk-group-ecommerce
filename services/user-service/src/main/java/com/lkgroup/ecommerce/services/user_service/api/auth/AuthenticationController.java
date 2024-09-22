package com.lkgroup.ecommerce.services.user_service.api.auth;

import com.lkgroup.ecommerce.common.domain.entities.User;
import com.lkgroup.ecommerce.common.domain.repositories.UserRepository;
import com.lkgroup.ecommerce.common.validation.validators.PathUUID;
import com.lkgroup.ecommerce.protobuf.userproto.AuthenticationProtos;
import com.lkgroup.ecommerce.protobuf.userproto.UsersProtos;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.NotFoundException;
import com.lkgroup.ecommerce.services.user_service.api.service.AuthenticationService;
import com.lkgroup.ecommerce.services.user_service.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AuthenticationController(AuthenticationService authenticationService, UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService, UserRepository userRepository1, PasswordEncoder passwordEncoder1, ModelMapper modelMapper) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository1;
        this.passwordEncoder = passwordEncoder1;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void authenticate(@RequestBody AuthenticationProtos.AuthenticationRequest authenticationRequest) {
        try {
            authenticationService.authenticate(
                    AuthenticationService.AuthenticationContext.builder()
                            .username(authenticationRequest.getUsername())
                            .password(authenticationRequest.getPassword())
                            .build()
            );
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void register(@Valid @RequestBody AuthenticationProtos.UserRegister model) {
       try {
           User user = modelMapper.map(model, User.class);
           user.setPassword(passwordEncoder.encode(user.getPassword()));
           userRepository.save(user);
       } catch (Exception e) {
           throw e;
       }
    }

    @Transactional
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
    }
}
