package com.mftplus.mftonlinestoreee.products.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductCategoryRequest(
        @Size(max = 30) String title,
        @Size(max = 200) String description,
        Boolean isActive,
        @Positive @Nullable Long parentId
) {
}
