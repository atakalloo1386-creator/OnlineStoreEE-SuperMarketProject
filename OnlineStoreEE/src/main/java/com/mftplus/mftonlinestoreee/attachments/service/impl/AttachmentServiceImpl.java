package com.mftplus.mftonlinestoreee.attachments.service.impl;

import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentContent;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentDto;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentRequest;
import com.mftplus.mftonlinestoreee.attachments.entity.Attachment;
import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import com.mftplus.mftonlinestoreee.attachments.exception.DuplicateFileNameException;
import com.mftplus.mftonlinestoreee.attachments.mapper.AttachmentMapper;
import com.mftplus.mftonlinestoreee.attachments.repository.AttachmentRepository;
import com.mftplus.mftonlinestoreee.attachments.service.AttachmentService;
import com.mftplus.mftonlinestoreee.attachments.storage.FileStorage;
import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class AttachmentServiceImpl implements AttachmentService {
    @Inject
    private AttachmentRepository repository;
    @Inject
    private AttachmentMapper mapper;
    @Inject
    private FileStorage fileStorage;

    @Transactional
    @Override
    public AttachmentDto create(AttachmentRequest request) {
        checkFileNameIsFree(request, null);
        Attachment attachment = mapper.toEntity(request);
        return mapper.toDto(repository.save(attachment));
    }

    @Override
    public Optional<AttachmentDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<AttachmentDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<AttachmentDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Override
    public List<AttachmentDto> findByOwner(AttachmentOwnerType ownerType, Long ownerId) {
        return mapper.toDtoList(repository.findByOwner(ownerType, ownerId));
    }

    @Transactional
    @Override
    public Optional<AttachmentDto> update(Long id, AttachmentRequest request) {
        return repository.findById(id)
                .map(attachment -> {
                    checkFileNameIsFree(request, id);
                    mapper.updateEntity(request, attachment);
                    return mapper.toDto(attachment);
                });
    }

    @Transactional
    @Override
    public Optional<AttachmentDto> deleteById(Long id) {
        return repository.findById(id)
                .map(attachment -> {
                    repository.delete(attachment);
                    return mapper.toDto(attachment);
                });
    }

    @Transactional
    @Override
    public Optional<AttachmentDto> uploadContent(Long id, InputStream content, String contentType) {
        return repository.findById(id)
                .map(attachment -> {
                    FileStorage.StoredFile file = fileStorage.save(content);
                    attachment.setStoredName(file.name());
                    attachment.setFileSize(file.size());
                    attachment.setContentType(contentType);
                    return mapper.toDto(attachment);
                });
    }

    @Override
    public Optional<AttachmentContent> findContent(Long id) {
        return repository.findById(id)
                .filter(attachment -> attachment.getStoredName() != null)
                .map(attachment -> new AttachmentContent(
                        attachment.getFileName(),
                        attachment.getContentType(),
                        attachment.getFileSize(),
                        fileStorage.open(attachment.getStoredName())));
    }

    private void checkFileNameIsFree(AttachmentRequest request, Long currentId) {
        repository.findByOwnerAndFileName(request.ownerType(), request.ownerId(), request.fileName())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateFileNameException(request.fileName());
                });
    }
}
