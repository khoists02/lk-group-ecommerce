package com.lkgroup.ecommerce.services.user_service.api;

import com.lkgroup.ecommerce.common.domain.entities.Category;
import com.lkgroup.ecommerce.common.domain.entities.Product;
import com.lkgroup.ecommerce.common.domain.repositories.CategoryRepository;
import com.lkgroup.ecommerce.common.domain.repositories.ProductRepository;
import com.lkgroup.ecommerce.protobuf.userproto.ProductProtos;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository, ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

//    @GetMapping

    @PostMapping
    @PreAuthorize("hasPermission('manageProduct')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createProduct(@Valid @RequestBody ProductProtos.ProductRequest request) {
        Product product = modelMapper.map(request, Product.class);
        Category category = categoryRepository.findById(UUID.fromString(request.getCategoryId())).orElseThrow(NotFoundException::new);
        product.setCategory(category);
        productRepository.save(product);
    }
}
