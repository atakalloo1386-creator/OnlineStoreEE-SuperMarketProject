package com.mftplus.mftonlinestoreee.tickets.dto;

import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String subject,
        String username,
        Long userID,
        TicketStatus ticketStatus
) {
}
