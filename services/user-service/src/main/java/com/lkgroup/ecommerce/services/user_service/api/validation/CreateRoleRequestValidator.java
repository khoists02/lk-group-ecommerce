package com.lkgroup.ecommerce.services.user_service.api.validation;

import com.lkgroup.ecommerce.common.validation.support.IsUUID;
import com.lkgroup.ecommerce.common.validation.support.ProtoValidator;
import com.lkgroup.ecommerce.common.validation.support.ValidatesProto;
import com.lkgroup.ecommerce.protobuf.userproto.RoleProtos;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@ValidatesProto(RoleProtos.CreateRoleRequest.class)
public class CreateRoleRequestValidator implements ProtoValidator {
    @NotBlank
    private String name;

    private String description;

    private List<@IsUUID String> permissions;
}
