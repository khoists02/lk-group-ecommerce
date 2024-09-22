package com.lkgroup.ecommerce.services.user_service.api.validation;

import com.lkgroup.ecommerce.common.validation.support.ProtoValidator;
import com.lkgroup.ecommerce.common.validation.support.ValidatesProto;
import com.lkgroup.ecommerce.protobuf.userproto.ProductProtos;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@ValidatesProto(ProductProtos.ProductRequest.class)
public class ProductRequestValidator implements ProtoValidator {
    @NotBlank
    private String productName;
}
