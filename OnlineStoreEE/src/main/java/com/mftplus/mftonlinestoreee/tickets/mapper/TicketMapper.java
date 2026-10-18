package com.mftplus.mftonlinestoreee.tickets.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class, builder = @Builder(disableBuilder = true))
public interface TicketMapper extends BaseMapper<Ticket, TicketDto, TicketRequest> {

    @Mapping(source = "user.id", target = "userID")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "status", target = "ticketStatus")
    TicketDto toDto(Ticket entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    Ticket toEntity(TicketRequest request);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(TicketRequest request, @MappingTarget Ticket entity);
}
