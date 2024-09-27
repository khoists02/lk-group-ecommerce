package com.lkgroup.ecommerce.common.domain.DTO;

import com.lkgroup.ecommerce.common.validation.support.IsUUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CreateRoleRequest {
    @NotBlank
    private String name;
    private String description;
    @Size(min = 1)
    private List<@IsUUID String> permissions;
}
