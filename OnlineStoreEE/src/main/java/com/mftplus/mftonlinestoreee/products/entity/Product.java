package com.mftplus.mftonlinestoreee.products.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@SQLDelete(sql = "update products set deleted=true,deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter


public class Product extends BaseEntity {


    @Column(name = "product_name", nullable = false, length = 30)
    private String name;

    @Column(name = "product_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;


    @Column(name = "product_description", length = 200)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

}
