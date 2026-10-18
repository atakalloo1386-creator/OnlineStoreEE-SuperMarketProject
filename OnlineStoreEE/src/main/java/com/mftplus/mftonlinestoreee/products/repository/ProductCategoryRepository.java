package com.mftplus.mftonlinestoreee.products.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.products.entity.ProductCategory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductCategoryRepository extends BaseRepository<ProductCategory> {
    public ProductCategoryRepository() {}

    public ProductCategoryRepository(EntityManager entityManager) { super(entityManager);}

    @Override
    protected Class<ProductCategory> entityType() {
        return ProductCategory.class;
    }

    public Optional<ProductCategory> findByTitle(String title) {return findOneBy("title", title);}

    public List<ProductCategory> findSubCategories(Long parentId) {
        return entityManager
                .createQuery("select pc from ProductCategory pc"
                        + " where pc.parentCategory.id = :parentId"
                        + " order by pc.id", ProductCategory.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    public long countSubCategories(Long parentId) {
        return entityManager
                .createQuery("select count(pc) from ProductCategory pc"
                        + " where pc.parentCategory.id = :parentId", Long.class)
                .setParameter("parentId", parentId)
                .getSingleResult();
    }
}

