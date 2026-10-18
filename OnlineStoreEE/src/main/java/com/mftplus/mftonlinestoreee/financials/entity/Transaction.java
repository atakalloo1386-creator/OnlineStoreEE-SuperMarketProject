package com.mftplus.mftonlinestoreee.financials.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.financials.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@SQLDelete(sql = "update transactions set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Transaction extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type",nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "amount",nullable = false, precision = 19, scale = 2)
    @Positive
    private BigDecimal amount;

    @Column(name = "description",length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id")
    private Cashier cashier;
}
