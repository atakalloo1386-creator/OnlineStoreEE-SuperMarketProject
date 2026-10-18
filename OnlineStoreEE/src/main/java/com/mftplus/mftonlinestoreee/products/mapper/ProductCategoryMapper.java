package com.mftplus.mftonlinestoreee.products.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryRequest;
import com.mftplus.mftonlinestoreee.products.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface ProductCategoryMapper extends BaseMapper<ProductCategory,ProductCategoryDto, ProductCategoryRequest> {
    @Mapping(target = "parentId", source = "parentCategory.id")
    ProductCategoryDto toDto(ProductCategory entity);

    @Mapping(target = "parentCategory", ignore = true)
    ProductCategory toEntity(ProductCategoryRequest request);

    @Mapping(target = "parentCategory", ignore = true)
    void updateEntity(
            ProductCategoryRequest request,
            @MappingTarget ProductCategory entity);
}
