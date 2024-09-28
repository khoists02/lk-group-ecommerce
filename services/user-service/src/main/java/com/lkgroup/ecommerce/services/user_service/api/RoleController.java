package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.common.domain.DTO.CreateRoleRequest;
import com.lkgroup.ecommerce.common.domain.entities.*;
import com.lkgroup.ecommerce.common.domain.repositories.PermissionRepository;
import com.lkgroup.ecommerce.common.domain.repositories.RoleRepository;
import com.lkgroup.ecommerce.common.domain.services.TransactionHandler;
import com.lkgroup.ecommerce.common.validation.validators.PathUUID;
import com.lkgroup.ecommerce.protobuf.userproto.RoleProtos;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.NotFoundException;
import com.lkgroup.ecommerce.services.user_service.api.service.ConfigurationService;
import jakarta.persistence.EntityGraph;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@RestController
@Validated
@RequestMapping("/roles")
public class RoleController {
    private Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleRepository roleRepository;
    private final TransactionHandler transactionHandler;
    private final PermissionRepository permissionRepository;

    private final ConfigurationService configurationService;

    public RoleController(final RoleRepository roleRepository, TransactionHandler transactionHandler, PermissionRepository permissionRepository, ConfigurationService configurationService) {
        this.roleRepository = roleRepository;
        this.transactionHandler = transactionHandler;
        this.permissionRepository = permissionRepository;
        this.configurationService = configurationService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('viewRole') or hasSuperAdmin()")
    public RoleProtos.RolesResponse getAllRoles() {
        RoleProtos.RolesResponse.Builder b = RoleProtos.RolesResponse.newBuilder();
        logger.info("Get Configuration Hostname {}", configurationService.getRootDomain());
        List<Role> roles = roleRepository.findAll();
        b.addAllContent(roles.stream().map(r -> {
            RoleProtos.RoleResponse.Builder builder = RoleProtos.RoleResponse.newBuilder();
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
    @PreAuthorize("hasPermission('manageRole') or hasSuperAdmin()")
    public RoleProtos.RoleResponse getRoleById(@PathVariable("roleId") @PathUUID String roleId) {
        EntityGraph<Role> entityGraph = roleRepository.createEntityGraph();
        entityGraph.addAttributeNodes(Role_.ROLE_PERMISSIONS);
        entityGraph.addSubgraph(Role_.ROLE_PERMISSIONS).addAttributeNodes(RolePermission_.PERMISSION);
        Role role = roleRepository.findById(UUID.fromString(roleId), entityGraph).orElseThrow((NotFoundException::new));

        // TODO: can't use mapper cause complex data sets for permissions.
        RoleProtos.RoleResponse.Builder builder = RoleProtos.RoleResponse.newBuilder();
        builder.setId(role.getId().toString());
        builder.setName(role.getName());
        builder.setDescription(role.getDescription());
        builder.addAllPermissions(role.getRolePermissions().stream().map(p -> p.getPermission().getId().toString()).toList());
        return builder.build();
    }

    /**
     * TODO: eror with @Valid
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageRole')")
    public void createRole(@Valid @RequestBody CreateRoleRequest request) throws BadRequestException {
        try {

            Role role = new Role();
            role.setName(request.getName());
            role.setDescription(request.getDescription());
            runRoleInTransition(request, role);
        } catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/{roleId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageRole') or hasSuperAdmin()")
    public void updateRole(@PathVariable("roleId") @PathUUID String roleId ,@Valid @RequestBody CreateRoleRequest request) {
        Role role = roleRepository.findById(UUID.fromString(roleId)).orElseThrow((NotFoundException::new));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        try {
            runRoleInTransition(request, role);
        } catch (Exception e) {
            throw e;
        }
    }

    private void runRoleInTransition (CreateRoleRequest request, Role role) {
        // TODO: why we need to run in @Transactional annotation.
        transactionHandler.runInTransaction((Supplier<Role>) () -> {
            request.getPermissions().forEach(o -> {
                RolePermission rp = new RolePermission();
                rp.setPermission(permissionRepository.getReferenceById(UUID.fromString(o)));
                role.addRolePermission(rp);
            });
            return roleRepository.save(role);
        });
    }

    @DeleteMapping("/{roleId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('manageRole') or hasSuperAdmin()")
    public void deleteRole(@PathVariable("roleId") @PathUUID String roleId) {
        Role role = roleRepository.findById(UUID.fromString(roleId)).orElseThrow((NotFoundException::new));
        roleRepository.delete(role);
    }
}
