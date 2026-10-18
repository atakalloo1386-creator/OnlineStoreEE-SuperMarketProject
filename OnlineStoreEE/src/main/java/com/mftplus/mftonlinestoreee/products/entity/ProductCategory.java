package com.mftplus.mftonlinestoreee.products.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;


@Entity
@Table(name = "product_categories")
@SQLDelete(sql = "update product_categories set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter

public class ProductCategory extends BaseEntity{

    @Column(name = "productCategory_title", nullable = false, length = 30)
    private String title;


    @Column(name = "productCategory_description", length = 200)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private ProductCategory parentCategory;

}
