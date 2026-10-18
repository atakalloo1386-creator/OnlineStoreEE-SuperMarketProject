package com.mftplus.mftonlinestoreee.attachments.dto;

import java.io.InputStream;

public record AttachmentContent(
        String fileName,
        String contentType,
        Long fileSize,
        InputStream data) {
}
