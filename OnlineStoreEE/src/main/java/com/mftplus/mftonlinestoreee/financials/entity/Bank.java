package com.mftplus.mftonlinestoreee.financials.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "banks")
@SQLDelete(sql = "update banks set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Bank extends BaseEntity {

    @Column(name= "bank_name" ,nullable = false, length = 100)
    private String bankName;


    @Column(name = "bank_code",nullable = false, length = 20, unique = true)
    private String bankCode;

    @OneToMany
    @Column(name = "bank_account",length = 100)
    private List<BankAccount> bankAccounts;
}
