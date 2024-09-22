package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.common.domain.entities.Product;
import com.lkgroup.ecommerce.common.domain.repositories.ProductRepository;
import com.lkgroup.ecommerce.protobuf.userproto.ProductProtos;
import com.lkgroup.ecommerce.services.user_service.api.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;


    public ProductController(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createProduct(@Valid @RequestBody ProductProtos.ProductRequest request) {
        Product product = modelMapper.map(request, Product.class);
        productRepository.save(product);
    }
}
