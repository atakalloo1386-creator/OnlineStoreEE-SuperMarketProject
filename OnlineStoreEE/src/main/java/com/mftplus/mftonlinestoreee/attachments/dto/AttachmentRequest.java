package com.mftplus.mftonlinestoreee.attachments.dto;

import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AttachmentRequest(
        @NotNull AttachmentOwnerType ownerType,
        @NotNull @Positive Long ownerId,
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 255) String fileName,
        @Size(max = 500) String description,
        @Size(max = 500) String keywords) {
}
