package com.mftplus.mftonlinestoreee.products.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.products.dto.ProductDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductRequest;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(config = MapStructConfig.class)
public interface ProductMapper extends BaseMapper<Product,ProductDto, ProductRequest> {
    @Mapping(target = "categoryId", source = "productCategory.id")
    @Mapping(target = "categoryTitle", source = "productCategory.title")
    ProductDto toDto(Product entity);

    @Mapping(target = "productCategory", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "productCategory", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product entity);

}
