package com.mftplus.mftonlinestoreee.products.dto;


import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        String description,
        Boolean isActive,
        Long categoryId,
        String categoryTitle
//        Long productCategoryId,
//        String productCategoryTitle,
//        ProductCategoryDto productCategoryDto
) {
}
