package com.mftplus.mftonlinestoreee.tickets.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.TicketMessage;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class, builder = @Builder(disableBuilder = true))
public interface TicketMessageMapper extends BaseMapper<TicketMessage, TicketMessageDto, TicketMessageRequest> {

    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.username", target = "senderUsername")
    TicketMessageDto toDto(TicketMessage entity);

    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "sender", ignore = true)
    TicketMessage toEntity(TicketMessageRequest request);

    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "sender", ignore = true)
    void updateEntity(TicketMessageRequest request, @MappingTarget TicketMessage entity);
}
