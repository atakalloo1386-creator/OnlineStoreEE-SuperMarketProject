package com.mftplus.mftonlinestoreee.core.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructConfig {
}
