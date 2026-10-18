package com.mftplus.mftonlinestoreee.tickets.mapper;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.users.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus.OPEN;
import static org.junit.jupiter.api.Assertions.*;

class TicketMapperTest {

    private final TicketMapper mapper = Mappers.getMapper(TicketMapper.class);
    private User user = TestDataFactory.createUser();

    @Test
    void toEntity() {
        TicketRequest request = new TicketRequest(
                LocalDateTime.of(2025, 3, 15, 14, 30),
                LocalDateTime.of(2026, 5, 15, 14, 30),
                "Financial Issue",
                user.getUsername(),
                user.getId(),
                OPEN);

        Ticket ticket = mapper.toEntity(request);

        assertNull(ticket.getId());
        assertEquals(LocalDateTime.of(2025, 3, 15, 14, 30), ticket.getCreatedAt());
        assertEquals(LocalDateTime.of(2026, 5, 15, 14, 30), ticket.getUpdatedAt());
        assertEquals("Financial Issue", ticket.getSubject());
        assertEquals(user.getUsername(), ticket.getUser().getUsername());
        assertEquals(user.getId(), ticket.getUser().getId());
        assertEquals(OPEN, ticket.getStatus());
    }

    @Test
    void toDto() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2025, 3, 15, 14, 30))
                .updatedAt(LocalDateTime.of(2026, 5, 15, 14, 30))
                .subject("Financial Issue")
                .user(user)
                .status(OPEN)
                .build();

        TicketDto dto = mapper.toDto(ticket);

        assertEquals(new TicketDto(
                1L,
                LocalDateTime.of(2025, 3, 15, 14, 30),
                LocalDateTime.of(2026, 5, 15, 14, 30),
                "Financial Issue",
                user.getUsername(),
                user.getId(),
                OPEN
        ), dto);
    }

    @Test
    void updateEntityKeepsId() {

        Ticket ticket = Ticket.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2024, 1, 10, 9, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 10, 9, 0))
                .subject("Financial Issue")
                .user(user)
                .status(OPEN)
                .build();

        TicketRequest request = new TicketRequest(
                LocalDateTime.of(2025, 3, 15, 14, 30),
                LocalDateTime.of(2026, 5, 15, 14, 30),
                "Updated Financial Issue",
                user.getUsername(),
                user.getId(),
                OPEN
        );

        mapper.updateEntity(request, ticket);

        assertEquals(1L, ticket.getId());
        assertEquals(LocalDateTime.of(2025, 3, 15, 14, 30), ticket.getCreatedAt());
        assertEquals(LocalDateTime.of(2026, 5, 15, 14, 30), ticket.getUpdatedAt());
        assertEquals("Updated Financial Issue", ticket.getSubject());
        assertEquals(user.getUsername(), ticket.getUser().getUsername());
        assertEquals(user.getId(), ticket.getUser().getId());
        assertEquals(OPEN, ticket.getStatus());
    }
}
