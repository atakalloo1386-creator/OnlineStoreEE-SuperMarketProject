package com.mftplus.mftonlinestoreee.products.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryRequest;

import java.util.List;


public interface ProductCategoryService extends BaseService<ProductCategoryRequest, ProductCategoryDto> {
    List<ProductCategoryDto> findSubCategories(Long parentId);
}
