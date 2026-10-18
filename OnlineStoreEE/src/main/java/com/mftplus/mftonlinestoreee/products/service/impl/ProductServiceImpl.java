package com.mftplus.mftonlinestoreee.products.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.products.dto.ProductDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductRequest;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import com.mftplus.mftonlinestoreee.products.entity.ProductCategory;
import com.mftplus.mftonlinestoreee.products.exception.CategoryNotFoundException;
import com.mftplus.mftonlinestoreee.products.exception.DuplicateProductNameException;
import com.mftplus.mftonlinestoreee.products.mapper.ProductMapper;
import com.mftplus.mftonlinestoreee.products.repository.ProductCategoryRepository;
import com.mftplus.mftonlinestoreee.products.repository.ProductRepository;
import com.mftplus.mftonlinestoreee.products.service.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductServiceImpl implements ProductService {
    @Inject
    private ProductRepository repository;

    @Inject
    private ProductCategoryRepository categoryRepository;

    @Inject
    private ProductMapper mapper;

    @Transactional
    @Override
    public ProductDto create(ProductRequest request) {

        checkNameIsFree(request, null);

        ProductCategory category = loadCategory(request.categoryId());

        Product product = mapper.toEntity(request);
        product.setProductCategory(category);

        return mapper.toDto(repository.save(product));
    }


    @Override
    public Optional<ProductDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }


    @Override
    public List<ProductDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public List<ProductDto> findByCategory(Long categoryId) {
        return mapper.toDtoList(repository.findByCategory(categoryId));
    }


    @Override
    public PageResult<ProductDto> findAll(int page, int size) {
        return repository.findPage(page, size)
                .map(mapper::toDto);
    }


    @Transactional
    @Override
    public Optional<ProductDto> update(Long id, ProductRequest request) {

        return repository.findById(id)
                .map(product -> {

                    checkNameIsFree(request, id);

                    ProductCategory category = loadCategory(request.categoryId());

                    mapper.updateEntity(request, product);
                    product.setProductCategory(category);

                    return mapper.toDto(product);
                });
    }


    @Transactional
    @Override
    public Optional<ProductDto> deleteById(Long id) {
        return repository.findById(id)
                .map( product -> {
                    repository.delete(product);
                    return mapper.toDto(product);
                });
    }
    private ProductCategory loadCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category " + categoryId)
                );
    }

    private void checkNameIsFree(ProductRequest request, Long currentId) {

        repository.findByName(request.name())
                .ifPresent(product -> {

                    if (currentId == null || !product.getId().equals(currentId)) {
                        throw new DuplicateProductNameException(request.name());
                    }

                });
    }
}
