package com.mftplus.mftonlinestoreee.attachments.rest;

import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentContent;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentDto;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentRequest;
import com.mftplus.mftonlinestoreee.attachments.service.AttachmentService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType.PRODUCT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AttachmentApiTest {

    @Mock
    private AttachmentService service;
    @InjectMocks
    private AttachmentApi api;

    private AutoCloseable mocks;

    private final AttachmentRequest request = new AttachmentRequest(PRODUCT, 5L, "Invoice", "a.pdf", null, null);
    private final AttachmentDto invoice = new AttachmentDto(1L, PRODUCT, 5L, "Invoice", "a.pdf", null, null, null, null);

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getAttachmentReturnsTheDto() {
        when(service.findById(1L)).thenReturn(Optional.of(invoice));

        assertEquals(invoice, api.getAttachment(1L));
    }

    @Test
    void getMissingAttachmentThrowsNotFound() {
        when(service.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.getAttachment(99L));
    }

    @Test
    void getAttachmentsOfOneOwner() {
        when(service.findByOwner(PRODUCT, 5L)).thenReturn(List.of(invoice));

        assertEquals(List.of(invoice), api.getAttachments(PRODUCT, 5L));
    }

    @Test
    void createAnswers201WithTheCreatedDto() {
        when(service.create(request)).thenReturn(invoice);

        Response response = api.createAttachment(request);

        assertEquals(201, response.getStatus());
        assertEquals(invoice, response.getEntity());
    }

    @Test
    void updateOfMissingAttachmentThrowsNotFound() {
        when(service.update(99L, request)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.updateAttachment(99L, request));
    }

    @Test
    void deleteAnswers204() {
        when(service.deleteById(1L)).thenReturn(Optional.of(invoice));

        assertEquals(204, api.deleteAttachment(1L).getStatus());
    }

    @Test
    void downloadAnswersTheFileWithItsTypeAndName() {
        ByteArrayInputStream data = new ByteArrayInputStream(new byte[]{1, 2, 3});
        when(service.findContent(1L)).thenReturn(Optional.of(new AttachmentContent("a.pdf", "application/pdf", 3L, data)));

        Response response = api.downloadContent(1L);

        assertEquals(200, response.getStatus());
        assertEquals(data, response.getEntity());
        assertEquals("application/pdf", response.getMediaType().toString());
        assertEquals("attachment; filename*=UTF-8''a.pdf", response.getHeaderString("Content-Disposition"));
    }

    @Test
    void downloadWithoutFileThrowsNotFound() {
        when(service.findContent(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.downloadContent(1L));
    }
}
