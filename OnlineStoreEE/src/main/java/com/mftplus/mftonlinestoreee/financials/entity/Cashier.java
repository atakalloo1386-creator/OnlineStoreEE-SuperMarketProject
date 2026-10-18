package com.mftplus.mftonlinestoreee.financials.entity;

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
@Table(name = "cashiers")
@SQLDelete(sql = "update cashiers set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Cashier extends BaseEntity {

    @Column(name = "cashier_name",nullable = false, length = 100)
    private String cashierName;

    @Column(name = "employee_code", nullable = false, length = 20, unique = true)
    private String employeeCode;
}
