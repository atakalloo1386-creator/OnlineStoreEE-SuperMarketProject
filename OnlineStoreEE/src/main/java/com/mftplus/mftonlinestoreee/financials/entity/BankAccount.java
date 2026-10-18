package com.mftplus.mftonlinestoreee.financials.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_accounts")
@SQLDelete(sql = "update bank_accounts set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class BankAccount extends BaseEntity {

    @Column(name = "account_number", nullable = false, length = 30, unique = true)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;


    @Builder.Default
    @Column(name = "balance",nullable = false, precision = 19, scale = 2)
    @Positive
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
}
