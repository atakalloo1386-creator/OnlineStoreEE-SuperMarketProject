package com.mftplus.mftonlinestoreee.financials.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "card_payments")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class CardPayment extends Payment {

    @Column(name = "card_last_digits", nullable = false, length = 4)
    private String cardLastDigits;

    @Column(name = "tracking_code", nullable = false, length = 50)
    private String trackingCode;
}
