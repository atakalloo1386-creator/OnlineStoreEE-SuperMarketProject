package com.mftplus.mftonlinestoreee.inventories.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "inventories")
@SQLDelete(sql = "update inventories set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class Inventory extends BaseEntity {

    @Column(name = "name", nullable = false, length = 60)
    private String name;


    @Column(name = "location", nullable = false, length = 60)
    private String location;

}