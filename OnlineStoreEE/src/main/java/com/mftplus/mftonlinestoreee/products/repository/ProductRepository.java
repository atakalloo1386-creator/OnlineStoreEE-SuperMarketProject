package com.mftplus.mftonlinestoreee.products.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository extends BaseRepository<Product> {
    public ProductRepository() {}

    public ProductRepository(EntityManager entityManager) {super(entityManager);}

    @Override
    protected Class<Product> entityType() {return Product.class;}

    public Optional<Product> findByName(String name) {return findOneBy("name", name);}

    public List<Product> findByCategory(Long categoryId) {
        return entityManager
                .createQuery("select p from Product p"
                        + " where p.productCategory.id = :categoryId"
                        + " order by p.id", Product.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public long countByCategory(Long categoryId) {
        return entityManager
                .createQuery("select count(p) from Product p"
                        + " where p.productCategory.id = :categoryId", Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
    }
}