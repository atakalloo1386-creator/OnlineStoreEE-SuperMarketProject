package com.mftplus.mftonlinestoreee.inventories.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "inventory_transactions")
@SQLDelete(sql="update inventory_transactions set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class InventoryTransaction extends BaseEntity {

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name="inventory_transaction_type", nullable = false, length = 10)
    private InventoryTransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name="reference_type", length = 10)
    private InventoryReferenceType referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reason", nullable = false, length = 150)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem inventoryItem;
}
