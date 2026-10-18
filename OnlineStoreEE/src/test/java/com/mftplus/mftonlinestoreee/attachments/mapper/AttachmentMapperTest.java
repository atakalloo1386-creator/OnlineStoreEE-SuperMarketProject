package com.mftplus.mftonlinestoreee.attachments.mapper;

import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentDto;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentRequest;
import com.mftplus.mftonlinestoreee.attachments.entity.Attachment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType.PRODUCT;
import static org.junit.jupiter.api.Assertions.*;

class AttachmentMapperTest {

    // The implementation MapStruct generated at compile time (AttachmentMapperImpl)
    private final AttachmentMapper mapper = Mappers.getMapper(AttachmentMapper.class);

    @Test
    void toEntity() {
        AttachmentRequest request = new AttachmentRequest(PRODUCT, 5L, "Invoice", "a.pdf", "first", "tax, 2026");

        Attachment attachment = mapper.toEntity(request);

        assertNull(attachment.getId());
        assertEquals(PRODUCT, attachment.getOwnerType());
        assertEquals(5L, attachment.getOwnerId());
        assertEquals("Invoice", attachment.getTitle());
        assertEquals("a.pdf", attachment.getFileName());
        assertEquals("tax, 2026", attachment.getKeywords());
        assertNull(attachment.getStoredName(), "the file details are filled only when the file is uploaded");
    }

    @Test
    void toDto() {
        Attachment attachment = Attachment.builder()
                .id(1L).ownerType(PRODUCT).ownerId(5L).title("Invoice").fileName("a.pdf")
                .contentType("application/pdf").fileSize(3L).storedName("stored-name")
                .build();

        AttachmentDto dto = mapper.toDto(attachment);

        assertEquals(new AttachmentDto(1L, PRODUCT, 5L, "Invoice", "a.pdf", null, null, "application/pdf", 3L), dto);
    }

    @Test
    void updateEntityKeepsIdVersionAndTheFile() {
        Attachment attachment = Attachment.builder()
                .id(1L).version(3L).ownerType(PRODUCT).ownerId(5L).title("Old").fileName("old.pdf")
                .storedName("stored-name").fileSize(3L)
                .build();

        mapper.updateEntity(new AttachmentRequest(PRODUCT, 5L, "New", "new.pdf", "edited", null), attachment);

        assertEquals("New", attachment.getTitle());
        assertEquals("new.pdf", attachment.getFileName());
        assertEquals(1L, attachment.getId());
        assertEquals(3L, attachment.getVersion());
        assertEquals("stored-name", attachment.getStoredName());
        assertEquals(3L, attachment.getFileSize());
    }
}
