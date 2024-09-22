package com.lkgroup.ecommerce.services.user_service.api.auth;

import com.lkgroup.ecommerce.common.domain.entities.User;
import com.lkgroup.ecommerce.common.domain.exceptions.BadRequestException;
import com.lkgroup.ecommerce.common.domain.repositories.UserRepository;
import com.lkgroup.ecommerce.protobuf.userproto.AuthenticationProtos;
import com.lkgroup.ecommerce.services.user_service.utils.UserUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/authenticatedUser")
public class AuthenticatedUserController {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public AuthenticatedUserController(UserRepository userRepository, ModelMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public AuthenticationProtos.AuthenticatedUserResponse getAuthenticatedUserDetails() {
        UUID userId = UserUtils.getAuthenticatedUser().getUserId();
        User user = userRepository.findById(userId).orElseThrow(BadRequestException::new);

        AuthenticationProtos.AuthenticatedUserResponse.Builder builder = AuthenticationProtos.AuthenticatedUserResponse.newBuilder()
                .setId(user.getId().toString())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setAddress(user.getAddress())
                .setPhone(user.getPhone())
                .setImagePath(user.getImagePath())
                .setEnabled(user.isEnabled());
//                .addAllPermissions(userRepository.getPermissionCodesForUser(UserUtils.getUser()).toList()) // TODO: will remove these permissions after worklist-api launched

        return builder.build();
    }

}
