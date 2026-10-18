package com.mftplus.mftonlinestoreee.attachments.dto;

import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;

public record AttachmentDto(
        Long id,
        AttachmentOwnerType ownerType,
        Long ownerId,
        String title,
        String fileName,
        String description,
        String keywords,
        String contentType,
        Long fileSize) {
}
