package com.mftplus.mftonlinestoreee.attachments.entity;

import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "attachments", indexes = @Index(columnList = "ownerType, ownerId"))
@SQLDelete(sql = "update attachments set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class Attachment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttachmentOwnerType ownerType;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String fileName;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String keywords;

    @Column(length = 100)
    private String contentType;

    private Long fileSize;

    @Column(length = 100)
    private String storedName;
}
