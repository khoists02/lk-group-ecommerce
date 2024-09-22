package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.protobuf.userproto.GenericProtos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public GenericProtos.ValidationError home() {
        GenericProtos.ValidationError.Builder builder = GenericProtos.ValidationError.newBuilder();
        builder.setMessage("home");
        return builder.build();
    }
}
