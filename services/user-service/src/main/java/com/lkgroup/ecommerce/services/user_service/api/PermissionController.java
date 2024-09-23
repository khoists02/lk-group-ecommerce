package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.common.domain.entities.Permission;
import com.lkgroup.ecommerce.common.domain.repositories.PermissionRepository;
import com.lkgroup.ecommerce.protobuf.userproto.UsersProtos;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    @PreAuthorize("hasPermission('viewRole')")
    public UsersProtos.PermissionsResponse getPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        UsersProtos.PermissionsResponse.Builder b = UsersProtos.PermissionsResponse.newBuilder();
        b.addAllContent(permissions.stream().map(p -> {
            UsersProtos.PermissionResponse.Builder r = UsersProtos.PermissionResponse.newBuilder();
            r.setId(p.getId().toString());
            r.setCode(p.getName());
            r.setDesc(p.getDescription());
            r.setGroupKey(p.getGroup());
            return r.build();
        }).toList());
        return b.build();
    }
}
