package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.common.domain.entities.*;
import com.lkgroup.ecommerce.common.domain.repositories.PermissionRepository;
import com.lkgroup.ecommerce.common.domain.repositories.RoleRepository;
import com.lkgroup.ecommerce.common.domain.services.TransactionHandler;
import com.lkgroup.ecommerce.common.validation.validators.PathUUID;
import com.lkgroup.ecommerce.protobuf.userproto.UsersProtos;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.NotFoundException;
import jakarta.persistence.EntityGraph;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@RestController
@RequestMapping("/roles")
@Validated
public class RoleController {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final TransactionHandler transactionHandler;
    private final PermissionRepository permissionRepository;

    public RoleController(final RoleRepository roleRepository, ModelMapper modelMapper, TransactionHandler transactionHandler, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.transactionHandler = transactionHandler;
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    @PreAuthorize("hasPermission('viewRole') or hasSuperAdmin()")
    public UsersProtos.RolesResponse getAllRoles() {
        UsersProtos.RolesResponse.Builder b = UsersProtos.RolesResponse.newBuilder();

        List<Role> roles = roleRepository.findAll();
        b.addAllContent(roles.stream().map(r -> {
            UsersProtos.RoleResponse.Builder builder = UsersProtos.RoleResponse.newBuilder();
            // TODO: convert name.
            builder.setId(r.getId().toString());
            builder.setName(r.getName());
            builder.setDescription(r.getDescription());
            return builder.build();
        }).toList());
        return b.build();
    }

    @GetMapping("/{roleId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission('viewRole')")
    public UsersProtos.RoleResponse getRoleById(@PathVariable("roleId") @PathUUID String roleId) {
        EntityGraph<Role> entityGraph = roleRepository.createEntityGraph();
        entityGraph.addAttributeNodes(Role_.ROLE_PERMISSIONS);
        entityGraph.addSubgraph(Role_.ROLE_PERMISSIONS).addAttributeNodes(RolePermission_.PERMISSION);
        Role role = roleRepository.findById(UUID.fromString(roleId), entityGraph).orElseThrow((NotFoundException::new));

        // TODO: can't use mapper cause complex data sets for permissions.
        UsersProtos.RoleResponse.Builder builder = UsersProtos.RoleResponse.newBuilder();
        builder.setId(role.getId().toString());
        builder.setName(role.getName());
        builder.setDescription(role.getDescription());
        builder.addAllPermissions(role.getRolePermissions().stream().map(p -> p.getPermission().getId().toString()).toList());
        return builder.build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageRole')")
    public void createRole(@Valid @RequestBody UsersProtos.RoleRequest request) {
        try {
            Role role = new Role();
            role.setName(request.getName());
            role.setDescription(request.getDescription());
            transactionHandler.runInTransaction((Supplier<Role>) () -> {
                request.getPermissionsList().forEach(o -> {
                    RolePermission rp = new RolePermission();
                    rp.setPermission(permissionRepository.getReferenceById(UUID.fromString(o)));
                    role.addRolePermission(rp);
                });
                return roleRepository.save(role);
            });
        } catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/{roleId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageRole')")
    public void updateRole(@PathVariable("roleId") @PathUUID String roleId , @RequestBody UsersProtos.RoleRequest request) {
        Role role = roleRepository.findById(UUID.fromString(roleId)).orElseThrow((NotFoundException::new));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        try {
            transactionHandler.runInTransaction((Supplier<Role>) () -> {
                request.getPermissionsList().forEach(o -> {
                    RolePermission rp = new RolePermission();
                    rp.setPermission(permissionRepository.getReferenceById(UUID.fromString(o)));
                    role.addRolePermission(rp);
                });
                return roleRepository.save(role);
            });
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{roleId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageRole')")
    public void deleteRole(@PathVariable("roleId") @PathUUID String roleId) {
        Role role = roleRepository.findById(UUID.fromString(roleId)).orElseThrow((NotFoundException::new));
        roleRepository.delete(role);
    }
}
