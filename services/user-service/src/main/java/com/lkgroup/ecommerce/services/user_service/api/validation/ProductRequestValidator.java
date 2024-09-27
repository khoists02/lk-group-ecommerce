package com.lkgroup.ecommerce.services.user_service.api.validation;

import com.lkgroup.ecommerce.common.validation.support.IsUUID;
import com.lkgroup.ecommerce.common.validation.support.ProtoValidator;
import com.lkgroup.ecommerce.common.validation.support.ValidatesProto;
import com.lkgroup.ecommerce.protobuf.userproto.ProductProtos;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidatesProto(ProductProtos.ProductRequest.class)
public class ProductRequestValidator implements ProtoValidator {
    @NotBlank
    private String productName;

    @NotNull
    @Min(0)
    private Long price;

    @IsUUID
    @NotNull
    private String categoryId ;
}
