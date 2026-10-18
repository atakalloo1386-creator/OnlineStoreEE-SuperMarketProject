package com.mftplus.mftonlinestoreee.inventories.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "inventory_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product",
                        columnNames = {"inventory_id", "product_id"}
                )
        }
)
@SQLDelete(sql = "update inventory_items set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class InventoryItem extends BaseEntity {

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "minimum_quantity", nullable = false)
    private int minimumQuantity;


    @Column(name = "batch_number", nullable = false, length = 60)
    private String batchNumber;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;
}