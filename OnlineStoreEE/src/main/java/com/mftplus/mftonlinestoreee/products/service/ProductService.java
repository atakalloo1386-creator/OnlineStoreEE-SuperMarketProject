package com.mftplus.mftonlinestoreee.products.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.products.dto.ProductDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductRequest;
import java.util.List;


public interface ProductService extends BaseService<ProductRequest, ProductDto> {
    List<ProductDto> findByCategory(Long categoryId);
}
