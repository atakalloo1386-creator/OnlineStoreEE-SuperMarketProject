package com.mftplus.mftonlinestoreee.organization.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.organization.entity.Organization;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;

@ApplicationScoped
public class OrganizationRepository extends BaseRepository<Organization> {

    public OrganizationRepository() {
    }

    public OrganizationRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Organization> entityType() {
        return Organization.class;
    }

    public Optional<Organization> findByRegistrationNumber(String registrationNumber) {
        return findOneBy("registrationNumber", registrationNumber);
    }
}
