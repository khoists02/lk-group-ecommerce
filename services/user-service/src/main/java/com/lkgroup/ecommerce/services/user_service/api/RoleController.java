package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.common.domain.entities.Permission;
import com.lkgroup.ecommerce.common.domain.entities.Role;
import com.lkgroup.ecommerce.common.domain.repositories.PermissionRepository;
import com.lkgroup.ecommerce.common.domain.repositories.RoleRepository;
import com.lkgroup.ecommerce.common.validation.validators.PathUUID;
import com.lkgroup.ecommerce.protobuf.userproto.UsersProtos;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
@Validated
public class RoleController {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PermissionRepository permissionRepository;

    public RoleController(final RoleRepository roleRepository, ModelMapper modelMapper, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    public UsersProtos.RolesResponse getAllRoles() {
        UsersProtos.RolesResponse.Builder b = UsersProtos.RolesResponse.newBuilder();

        List<Role> roles = roleRepository.findAll();
        b.addAllContent(roles.stream().map(r -> {
            UsersProtos.RoleResponse.Builder builder = modelMapper.map(r, UsersProtos.RoleResponse.Builder.class);
            // TODO: convert name.
            builder.setId(r.getId().toString());
            return builder.build();
        }).toList());
        return b.build();
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageUser')")
    public void createRole(@Valid @RequestBody UsersProtos.RoleRequest request) {
        // TODO: Permisisons
        Set<Permission> permissions = request.getPermissionIdsList().stream().map(p -> permissionRepository.findById(UUID.fromString(p)).orElse(null)).collect(Collectors.toSet());

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.addPermissions(permissions);

        roleRepository.save(role);
    }

    @PutMapping("/{roleId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageUser')")
    public void updateRole(@PathVariable("roleId") @PathUUID String roleId , @Valid @RequestBody UsersProtos.RoleRequest request) {
        Role role = roleRepository.findById(UUID.fromString(roleId)).orElseThrow((NotFoundException::new));
        // TODO: Permisisons
        Set<Permission> permissions = request.getPermissionIdsList().stream().map(p -> permissionRepository.findById(UUID.fromString(p)).orElse(null)).collect(Collectors.toSet());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.addPermissions(permissions);

        roleRepository.save(role);
    }

    @DeleteMapping("/{roleId")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageUser')")
    public void deleteRole(@PathVariable("roleId") @PathUUID String roleId) {
        Role role = roleRepository.findById(UUID.fromString(roleId)).orElseThrow((NotFoundException::new));
        roleRepository.delete(role);
    }
}
