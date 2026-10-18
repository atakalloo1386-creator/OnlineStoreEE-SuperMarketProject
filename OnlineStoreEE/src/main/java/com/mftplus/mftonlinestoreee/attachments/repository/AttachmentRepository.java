package com.mftplus.mftonlinestoreee.attachments.repository;

import com.mftplus.mftonlinestoreee.attachments.entity.Attachment;
import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AttachmentRepository extends BaseRepository<Attachment> {

    public AttachmentRepository() {
    }

    public AttachmentRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Attachment> entityType() {
        return Attachment.class;
    }

    public List<Attachment> findByOwner(AttachmentOwnerType ownerType, Long ownerId) {
        return entityManager
                .createQuery("select a from Attachment a"
                        + " where a.ownerType = :ownerType and a.ownerId = :ownerId"
                        + " order by a.id", Attachment.class)
                .setParameter("ownerType", ownerType)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }

    public Optional<Attachment> findByOwnerAndFileName(AttachmentOwnerType ownerType, Long ownerId, String fileName) {
        return entityManager
                .createQuery("select a from Attachment a"
                        + " where a.ownerType = :ownerType and a.ownerId = :ownerId"
                        + " and a.fileName = :fileName", Attachment.class)
                .setParameter("ownerType", ownerType)
                .setParameter("ownerId", ownerId)
                .setParameter("fileName", fileName)
                .getResultStream()
                .findFirst();
    }
}
