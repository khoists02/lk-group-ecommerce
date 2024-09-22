package com.lkgroup.ecommerce.services.user_service.api.validation;

import com.lkgroup.ecommerce.common.validation.support.ProtoValidator;
import com.lkgroup.ecommerce.common.validation.support.ValidatesProto;
import com.lkgroup.ecommerce.protobuf.userproto.AuthenticationProtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
@ValidatesProto(AuthenticationProtos.UserRegister.class)
public class UserRegisterValidator implements ProtoValidator {
    @NotBlank
    private String username;

    private String name;

    @NotBlank
    private String password;

    @Email(message = "Email invalid format.")
    private String email;
}