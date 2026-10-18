package com.mftplus.mftonlinestoreee.products.dto;

public record ProductCategoryDto(
        Long id,
        String title,
        String description,
//        Boolean isActive,
        Boolean active,
        Long parentId
) {
}
