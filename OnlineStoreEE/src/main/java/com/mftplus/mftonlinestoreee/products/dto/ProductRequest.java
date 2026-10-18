package com.mftplus.mftonlinestoreee.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 30) String name,
        @NotNull @Positive BigDecimal price,
        @Size(max = 200) String description,
        Boolean active,
        @NotNull @Positive Long categoryId
//        Boolean isActive,
//        Long productCategoryDto,
        ) {
}

