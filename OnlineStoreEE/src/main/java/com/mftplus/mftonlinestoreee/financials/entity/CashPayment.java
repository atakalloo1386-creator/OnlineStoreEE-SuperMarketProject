package com.mftplus.mftonlinestoreee.financials.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cash_payments")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class CashPayment extends Payment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private Cashier cashier;
}
