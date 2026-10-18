package com.mftplus.mftonlinestoreee.attachments.service;

import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentDto;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentRequest;
import com.mftplus.mftonlinestoreee.attachments.entity.Attachment;
import com.mftplus.mftonlinestoreee.attachments.exception.DuplicateFileNameException;
import com.mftplus.mftonlinestoreee.attachments.mapper.AttachmentMapper;
import com.mftplus.mftonlinestoreee.attachments.repository.AttachmentRepository;
import com.mftplus.mftonlinestoreee.attachments.service.impl.AttachmentServiceImpl;
import com.mftplus.mftonlinestoreee.attachments.storage.FileStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType.PRODUCT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository repository;                                  // fake: no database
    @Mock
    private FileStorage fileStorage;                                          // fake: no disk
    @Spy
    private AttachmentMapper mapper = Mappers.getMapper(AttachmentMapper.class);  // the real mapper
    @InjectMocks
    private AttachmentServiceImpl service;                                    // gets all three in its @Inject fields

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createSavesAndReturnsTheDto() {
        when(repository.findByOwnerAndFileName(PRODUCT, 5L, "a.pdf")).thenReturn(Optional.empty());
        when(repository.save(any(Attachment.class))).thenAnswer(call -> {
            Attachment attachment = call.getArgument(0);
            attachment.setId(1L);   // what the database would do
            return attachment;
        });

        AttachmentDto dto = service.create(request("Invoice", "a.pdf"));

        assertEquals(1L, dto.id());
        assertEquals("a.pdf", dto.fileName());
        assertNull(dto.fileSize(), "no file yet");
    }

    @Test
    void createWithTakenFileNameFails() {
        when(repository.findByOwnerAndFileName(PRODUCT, 5L, "a.pdf")).thenReturn(Optional.of(attachment(1L, "a.pdf")));

        assertThrows(DuplicateFileNameException.class, () -> service.create(request("Other", "a.pdf")));
        verify(repository, never()).save(any());
    }

    @Test
    void updateMayKeepItsOwnFileName() {
        Attachment existing = attachment(1L, "a.pdf");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByOwnerAndFileName(PRODUCT, 5L, "a.pdf")).thenReturn(Optional.of(existing));

        Optional<AttachmentDto> dto = service.update(1L, request("Invoice v2", "a.pdf"));

        assertEquals("Invoice v2", dto.orElseThrow().title());
    }

    @Test
    void updateToTheFileNameOfAnotherAttachmentFails() {
        when(repository.findById(1L)).thenReturn(Optional.of(attachment(1L, "a.pdf")));
        when(repository.findByOwnerAndFileName(PRODUCT, 5L, "b.png")).thenReturn(Optional.of(attachment(2L, "b.png")));

        assertThrows(DuplicateFileNameException.class, () -> service.update(1L, request("Logo", "b.png")));
    }

    @Test
    void updateOfMissingAttachmentReturnsEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.update(99L, request("x", "x.pdf")).isEmpty());
    }

    @Test
    void deleteRemovesTheAttachment() {
        Attachment existing = attachment(1L, "a.pdf");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertTrue(service.deleteById(1L).isPresent());
        verify(repository).delete(existing);
    }

    @Test
    void uploadContentStoresTheFileAndItsDetails() {
        Attachment existing = attachment(1L, "a.pdf");
        InputStream content = new ByteArrayInputStream(new byte[]{1, 2, 3});
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(fileStorage.save(content)).thenReturn(new FileStorage.StoredFile("stored-name", 3));

        AttachmentDto dto = service.uploadContent(1L, content, "application/pdf").orElseThrow();

        assertEquals("application/pdf", dto.contentType());
        assertEquals(3L, dto.fileSize());
        assertEquals("stored-name", existing.getStoredName());
    }

    @Test
    void attachmentWithoutFileHasNoContent() {
        when(repository.findById(1L)).thenReturn(Optional.of(attachment(1L, "a.pdf")));

        assertTrue(service.findContent(1L).isEmpty());
        verifyNoInteractions(fileStorage);
    }

    private AttachmentRequest request(String title, String fileName) {
        return new AttachmentRequest(PRODUCT, 5L, title, fileName, null, null);
    }

    private Attachment attachment(Long id, String fileName) {
        return Attachment.builder().id(id).ownerType(PRODUCT).ownerId(5L).title("title").fileName(fileName).build();
    }
}
