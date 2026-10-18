package com.mftplus.mftonlinestoreee.organization.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.organization.entity.enums.BranchStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "branches")
@SQLDelete(sql = "update branches set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Branch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "branch_code", nullable = false, length = 50)
    private String branchCode;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String website;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BranchStatus status = BranchStatus.ACTIVE;

    @Column(length = 150)
    private String address;

    public void activate() {
        this.status = BranchStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = BranchStatus.INACTIVE;
    }
}
