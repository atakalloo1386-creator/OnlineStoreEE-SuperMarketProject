package com.mftplus.mftonlinestoreee.core.repository;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T extends BaseEntity> {

    @Inject
    protected EntityManager entityManager;

    protected BaseRepository() {
    }

    protected BaseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected abstract Class<T> entityType();

    protected String entityName() {
        return entityManager.getMetamodel().entity(entityType()).getName();
    }

    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T update(T entity){
        return entityManager.merge(entity);
    }

    public Optional<T> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery("select e from " + entityName() + " e where e.id = :id", entityType())
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public List<T> findAll() {
        return entityManager
                .createQuery("select e from " + entityName() + " e order by e.id", entityType())
                .getResultList();
    }

    public List<T> findAll(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must not be negative: " + page);
        }
        if (size < 1) {
            throw new IllegalArgumentException("size must be at least 1: " + size);
        }
        return entityManager
                .createQuery("select e from " + entityName() + " e order by e.id", entityType())
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public PageResult<T> findPage(int page, int size) {
        return PageResult.of(findAll(page, size), page, size, count());
    }

    public long count() {
        return entityManager
                .createQuery("select count(e) from " + entityName() + " e", Long.class)
                .getSingleResult();
    }

    public void delete(T entity) {
        entityManager.remove(entity);
    }

    public boolean delete(Long id) {
        return findById(id)
                .map(entity -> {
                    entityManager.remove(entity);
                    return true;
                })
                .orElse(false);
    }

    public Optional<T> restore(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        int restored = entityManager
                .createNativeQuery("update " + tableName()
                        + " set deleted = false, deleted_at = null, version = version + 1"
                        + " where id = ?1")
                .setParameter(1, id)
                .executeUpdate();

        return restored == 1 ? findById(id) : Optional.empty();
    }

    private String tableName() {
        Table table = entityType().getAnnotation(Table.class);
        if (table == null || table.name().isBlank()) {
            throw new IllegalStateException(
                    entityName() + " needs @Table(name = ...) before it can be restored");
        }
        return table.name();
    }

    protected Optional<T> findOneBy(String field, Object value) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityType());
        Root<T> root = query.from(entityType());

        query.select(root).where(builder.equal(root.get(field), value));

        return entityManager.createQuery(query).getResultStream().findFirst();
    }
}
