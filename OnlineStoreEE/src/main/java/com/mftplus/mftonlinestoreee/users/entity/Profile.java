package com.mftplus.mftonlinestoreee.users.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;


@Table(name="profiles")
@Entity
@SQLDelete(sql = "update profiles set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder


public class Profile extends BaseEntity {
    @Column(name="first_name",nullable = false,length = 30)
    private String firstName;

    @Column(name="last_name",nullable = false,length = 30)
    private String lastName;

    @Column(name="phone_number",length = 12)
    private String phoneNumber;

    @Column(name="birth_date")
    private LocalDate birthDate;

    @Column(name="address",length = 100)
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


}
