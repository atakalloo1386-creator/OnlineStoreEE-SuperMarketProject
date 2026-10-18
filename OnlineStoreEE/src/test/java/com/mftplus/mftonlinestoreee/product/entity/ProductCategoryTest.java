package com.mftplus.mftonlinestoreee.product.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class ProductCategoryTest {
    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("TestPU");
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }
}
