package com.mftplus.mftonlinestoreee.tickets.dto;

import java.time.LocalDateTime;

public record TicketMessageDto (
        Long id,
        LocalDateTime createdAt,
        Long ticketId,
        Long senderId,
        String senderUsername,
        String content
) {
}
