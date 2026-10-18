package com.mftplus.mftonlinestoreee.attachments.service;

import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentContent;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentDto;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentRequest;
import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import com.mftplus.mftonlinestoreee.core.service.BaseService;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface AttachmentService extends BaseService<AttachmentRequest, AttachmentDto> {

    List<AttachmentDto> findByOwner(AttachmentOwnerType ownerType, Long ownerId);

    Optional<AttachmentDto> uploadContent(Long id, InputStream content, String contentType);

    Optional<AttachmentContent> findContent(Long id);
}
