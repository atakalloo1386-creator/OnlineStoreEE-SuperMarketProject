package com.mftplus.mftonlinestoreee.financials.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "cheque_payments")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ChequePayment extends Payment {

    @Column(name = "cheque_number", nullable = false, length = 20)
    private String chequeNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
}
