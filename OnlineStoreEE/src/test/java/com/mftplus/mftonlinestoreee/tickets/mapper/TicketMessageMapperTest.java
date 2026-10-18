package com.mftplus.mftonlinestoreee.tickets.mapper;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.TicketMessage;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.users.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketMessageMapperTest {

    private final TicketMessageMapper mapper = Mappers.getMapper(TicketMessageMapper.class);
    private final User user = TestDataFactory.createUser();

    @Test
    void toEntity() {
        TicketMessageRequest request = new TicketMessageRequest(
                LocalDateTime.of(2025, 3, 15, 14, 30),
                10L,
                user.getId(),
                "This is a test message"
        );

        TicketMessage message = mapper.toEntity(request);

        assertNull(message.getId());
        assertEquals(LocalDateTime.of(2025, 3, 15, 14, 30), message.getCreatedAt());
        assertEquals(10L, message.getTicket().getId());
        assertEquals(user.getId(), message.getSender().getId());
        assertEquals("This is a test message", message.getContent());
    }

    @Test
    void toDto() {
        TicketMessage message = TicketMessage.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2025, 3, 15, 14, 30))
                .ticket(TestDataFactory.createTicket())
                .sender(user)
                .content("This is a test message")
                .build();

        TicketMessageDto dto = mapper.toDto(message);

        assertEquals(new TicketMessageDto(
                1L,
                LocalDateTime.of(2025, 3, 15, 14, 30),
                message.getTicket().getId(),
                user.getId(),
                user.getUsername(),
                "This is a test message"
        ), dto);
    }

    @Test
    void updateEntityKeepsId() {
        TicketMessage message = TicketMessage.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2024, 1, 10, 9, 0))
                .ticket(TestDataFactory.createTicket())
                .sender(user)
                .content("Old message content")
                .build();

        TicketMessageRequest request = new TicketMessageRequest(
                LocalDateTime.of(2025, 3, 15, 14, 30),
                10L,
                user.getId(),
                "Updated message content"
        );

        mapper.updateEntity(request, message);

        assertEquals(1L, message.getId());
        assertEquals(LocalDateTime.of(2025, 3, 15, 14, 30), message.getCreatedAt());
        assertEquals(10L, message.getTicket().getId());
        assertEquals(user.getId(), message.getSender().getId());
        assertEquals("Updated message content", message.getContent());
    }
}
