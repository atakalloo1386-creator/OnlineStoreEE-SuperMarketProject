package com.mftplus.mftonlinestoreee.organization.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.organization.entity.enums.DepartmentStatus;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@SQLDelete(sql = "update departments set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Department extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DepartmentStatus status = DepartmentStatus.ACTIVE;

    @ManyToMany
    @JoinTable(
            name = "department_employees",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> employees = new ArrayList<>();

    public void activate() {
        this.status = DepartmentStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = DepartmentStatus.INACTIVE;
    }
}
