package com.mftplus.mftonlinestoreee.delivery.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "couriers")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public class Courier extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 15)
    private String phone;

    @Email(message = "Invalid email")
    private String email;

    @Column(name = "vehicle_type", nullable = false, length = 200)
    private String vehicleType;
}
