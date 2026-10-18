package com.mftplus.mftonlinestoreee.products.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryRequest;
import com.mftplus.mftonlinestoreee.products.entity.ProductCategory;
import com.mftplus.mftonlinestoreee.products.exception.CategoryInUseException;
import com.mftplus.mftonlinestoreee.products.exception.CategoryNotFoundException;
import com.mftplus.mftonlinestoreee.products.exception.DuplicateCategoryTitleException;
import com.mftplus.mftonlinestoreee.products.exception.InvalidParentCategoryException;
import com.mftplus.mftonlinestoreee.products.mapper.ProductCategoryMapper;
import com.mftplus.mftonlinestoreee.products.repository.ProductCategoryRepository;
import com.mftplus.mftonlinestoreee.products.repository.ProductRepository;
import com.mftplus.mftonlinestoreee.products.service.ProductCategoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Inject
    private ProductCategoryRepository repository;

    @Inject
    private ProductRepository productRepository;

    @Inject
    private ProductCategoryMapper mapper;

    @Transactional
    @Override
    public ProductCategoryDto create(ProductCategoryRequest request) {

        checkTitleIsFree(request, null);

        ProductCategory parent = loadParent(request.parentId(), null);

        ProductCategory productCategory = mapper.toEntity(request);
        productCategory.setParentCategory(parent);

        return mapper.toDto(repository.save(productCategory));
    }

    @Override
    public Optional<ProductCategoryDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ProductCategoryDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public List<ProductCategoryDto> findSubCategories(Long parentId) {
        return mapper.toDtoList(repository.findSubCategories(parentId));
    }

    @Override
    public PageResult<ProductCategoryDto> findAll(int page, int size) {
        return repository.findPage(page, size)
                .map(mapper::toDto);
    }



    @Transactional
    @Override
    public Optional<ProductCategoryDto> update(
            Long id,
            ProductCategoryRequest request
    ) {
        return repository.findById(id)
                .map(productCategory -> {

                    checkTitleIsFree(request, id);

                    ProductCategory parent =
                            loadParent(request.parentId(), id);

                    mapper.updateEntity(request, productCategory);

                    productCategory.setParentCategory(parent);

                    return mapper.toDto(productCategory);
                });
    }

    @Transactional
    @Override
    public Optional<ProductCategoryDto> deleteById(Long id) {
        return repository.findById(id)
                .map(productCategory -> {

                    checkNotInUse(id);

                    repository.delete(productCategory);

                    return mapper.toDto(productCategory);
                });
    }
    private void checkTitleIsFree(ProductCategoryRequest request, Long currentId) {
        repository.findByTitle(request.title())
                .ifPresent(productCategory -> {
                    if (currentId == null || !productCategory.getId().equals(currentId)) {
                        throw new DuplicateCategoryTitleException(request.title());
                    }
                });
    }
    private ProductCategory loadParent(Long parentId, Long currentId) {
        if (parentId == null) {
            return null;
        }

        if (currentId != null && parentId.equals(currentId)) {
            throw new InvalidParentCategoryException(String.valueOf(parentId));
        }

        return repository.findById(parentId)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category " + parentId)
                );
    }
    private void checkNotInUse(Long id) {
        long productCount = productRepository.countByCategory(id);
        long subCategoryCount = repository.countSubCategories(id);

        if (productCount > 0 || subCategoryCount > 0) {
            throw new CategoryInUseException(
                    "Category " + id + " is in use"
            );
        }
    }
}
