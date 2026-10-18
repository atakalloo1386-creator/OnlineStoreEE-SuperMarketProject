package com.mftplus.mftonlinestoreee.tickets.dto;

import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TicketRequest(
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @NotBlank @Size(max = 150) String subject,
        @NotBlank String username,
        @NotNull Long userId,
        @NotNull TicketStatus status
) {
}
