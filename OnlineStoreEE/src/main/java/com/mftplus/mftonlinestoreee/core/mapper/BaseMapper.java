package com.mftplus.mftonlinestoreee.core.mapper;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface BaseMapper<E extends BaseEntity, D, R> {

    D toDto(E entity);

    List<D> toDtoList(List<E> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    E toEntity(R request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(R request, @MappingTarget E entity);
}
