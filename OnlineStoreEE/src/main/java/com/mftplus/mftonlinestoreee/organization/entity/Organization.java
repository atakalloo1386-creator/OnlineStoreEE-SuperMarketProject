package com.mftplus.mftonlinestoreee.organization.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.organization.entity.enums.OrganizationStatus;
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
@Table(name = "organizations")
@SQLDelete(sql = "update organizations set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Organization extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "registration_number", nullable = false, length = 50)
    private String registrationNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String website;

    @Column(name = "establishment_date")
    private LocalDate establishmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    public void activate() {
        this.status = OrganizationStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = OrganizationStatus.INACTIVE;
    }
}
