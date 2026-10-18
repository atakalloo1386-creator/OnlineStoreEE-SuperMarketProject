package com.mftplus.mftonlinestoreee.organization.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.organization.entity.Branch;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BranchRepository extends BaseRepository<Branch> {

    public BranchRepository() {
    }

    public BranchRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Branch> entityType() {
        return Branch.class;
    }

    public List<Branch> findByOrganizationId(Long organizationId) {
        return entityManager
                .createQuery("select b from Branch b"
                        + " where b.organization.id = :organizationId"
                        + " order by b.id", Branch.class)
                .setParameter("organizationId", organizationId)
                .getResultList();
    }

    public long countByOrganizationId(Long organizationId) {
        return entityManager
                .createQuery("select count(b) from Branch b"
                        + " where b.organization.id = :organizationId", Long.class)
                .setParameter("organizationId", organizationId)
                .getSingleResult();
    }

    public Optional<Branch> findByBranchCode(String branchCode) {
        return findOneBy("branchCode", branchCode);
    }
}
