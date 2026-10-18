package com.mftplus.mftonlinestoreee.attachments.mapper;

import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentDto;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentRequest;
import com.mftplus.mftonlinestoreee.attachments.entity.Attachment;
import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface AttachmentMapper extends BaseMapper<Attachment, AttachmentDto, AttachmentRequest> {
}
