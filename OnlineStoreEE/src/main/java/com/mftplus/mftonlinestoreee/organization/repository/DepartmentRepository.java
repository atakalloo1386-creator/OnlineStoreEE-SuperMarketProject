package com.mftplus.mftonlinestoreee.organization.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.organization.entity.Department;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DepartmentRepository extends BaseRepository<Department> {

    public DepartmentRepository() {
    }

    public DepartmentRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Department> entityType() {
        return Department.class;
    }

    public List<Department> findByBranchId(Long branchId) {
        return entityManager
                .createQuery("select d from Department d"
                        + " where d.branch.id = :branchId"
                        + " order by d.id", Department.class)
                .setParameter("branchId", branchId)
                .getResultList();
    }

    public long countByBranchId(Long branchId) {
        return entityManager
                .createQuery("select count(d) from Department d"
                        + " where d.branch.id = :branchId", Long.class)
                .setParameter("branchId", branchId)
                .getSingleResult();
    }

    public Optional<Department> findByBranchIdAndCode(Long branchId, String code) {
        return entityManager
                .createQuery("select d from Department d"
                        + " where d.branch.id = :branchId and d.code = :code", Department.class)
                .setParameter("branchId", branchId)
                .setParameter("code", code)
                .getResultStream()
                .findFirst();
    }

    public List<Department> findByEmployeeId(Long userId) {
        return entityManager
                .createQuery("select d from Department d join d.employees e"
                        + " where e.id = :userId"
                        + " order by d.id", Department.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
