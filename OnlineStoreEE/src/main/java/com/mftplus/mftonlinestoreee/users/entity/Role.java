package com.mftplus.mftonlinestoreee.users.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@SQLDelete(sql = "update roles set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")


@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Role extends BaseEntity {

    @Column(nullable = false,unique = true,length = 50)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

}
