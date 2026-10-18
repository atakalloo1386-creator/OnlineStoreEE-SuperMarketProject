package com.mftplus.mftonlinestoreee.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TicketMessageRequest (
        LocalDateTime createdAt,
        @NotNull Long ticketId,
        @NotNull Long senderId,
        @NotBlank @Size(max = 2000) String content
) {
}
