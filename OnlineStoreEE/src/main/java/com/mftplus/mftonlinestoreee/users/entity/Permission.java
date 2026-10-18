package com.mftplus.mftonlinestoreee.users.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "permissions")
@SQLDelete(sql = "update permissions set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
