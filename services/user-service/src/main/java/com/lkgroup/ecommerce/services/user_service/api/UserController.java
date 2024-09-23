package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.common.domain.entities.User;
import com.lkgroup.ecommerce.common.domain.repositories.UserRepository;
import com.lkgroup.ecommerce.common.validation.validators.PathUUID;
import com.lkgroup.ecommerce.protobuf.userproto.UsersProtos;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserController(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    // TODO: will implete filtering on another branch.
    @PreAuthorize("hasPermission('viewUser')")
    public UsersProtos.UsersResponse getAllUsers() {
        UsersProtos.UsersResponse.Builder usersBuilder = UsersProtos.UsersResponse.newBuilder();
        List<User> users = userRepository.findAll();
        usersBuilder.addAllContent(users.stream().map(u -> {
            UsersProtos.UserResponse.Builder builder = modelMapper.map(u, UsersProtos.UserResponse.Builder.class);
            return builder.build();
        }).toList());
        return usersBuilder.build();
    }


    /**
     * URL: https://api.xx/users/{userId}/enabled
     * PUT
     * @param userId
     * @param request UsersProtos.EnabledUser
     * @Authorize manageUser
     */
    @PutMapping("/{userId}/enabled")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageUser')")
    // TODO: enabled user accout, admin role only.
    public void enabledUser(@PathVariable("userId") @PathUUID String userId, @RequestBody UsersProtos.EnabledUser request) {
        // User will update.
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(NotFoundException::new);
        user.setEnabled(request.getEnabled());
        userRepository.saveAndFlush(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageUser')")
    public void deleteUser(@PathVariable("userId") @PathUUID String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(NotFoundException::new);
        userRepository.delete(user);
    }
}
