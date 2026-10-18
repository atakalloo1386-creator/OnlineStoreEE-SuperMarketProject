package com.mftplus.mftonlinestoreee.delivery.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;


@Entity
@Table(name = "deliveries")
@SQLDelete(sql = "update deliveries set deleted=true,deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString(exclude = "courier")
public class Delivery extends BaseEntity {

    @Column(nullable = false, length = 300)
    private String address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.PREPARING;

    @Column(name = "tracking_number", nullable = false, length = 60, unique = true)
    private String trackingNumber;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "receive_date")
    private LocalDateTime receiveDate;

    @Column(name = "delivery_notes", length = 500)
    private String deliveryNote;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id" , nullable = false)
    private Courier courier;

    public void send() {
        this.status = DeliveryStatus.SENT;
        this.sendDate = LocalDateTime.now();
    }

    public void confirmDelivery() {
        this.status = DeliveryStatus.DELIVERED;
        this.receiveDate = LocalDateTime.now();

    }
}
