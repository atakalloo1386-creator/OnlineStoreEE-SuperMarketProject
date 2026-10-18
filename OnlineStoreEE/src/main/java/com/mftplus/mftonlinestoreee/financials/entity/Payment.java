package com.mftplus.mftonlinestoreee.financials.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentType;
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
@Table(name = "payments")
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql = "update payments set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Payment extends BaseEntity {

    @Column(name = "amount",nullable = false)
    @Positive
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false, length = 20)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type",nullable = false, length = 20)
    private PaymentType type;


    @Column(name = "order_id", nullable = false)
    private Long orderId;
}
