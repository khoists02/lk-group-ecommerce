package com.lkgroup.ecommerce.services.user_service.api.validation;

import com.lkgroup.ecommerce.common.validation.support.ProtoValidator;
import com.lkgroup.ecommerce.common.validation.support.ValidatesProto;
import com.lkgroup.ecommerce.protobuf.userproto.UsersProtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@ValidatesProto(UsersProtos.RoleRequest.class)
public class RoleRequestValidator implements ProtoValidator {
    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 1)
    private List<String> descriptionIds;
}
