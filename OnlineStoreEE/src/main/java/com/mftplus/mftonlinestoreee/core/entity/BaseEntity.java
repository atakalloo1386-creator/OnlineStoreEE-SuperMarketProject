package com.mftplus.mftonlinestoreee.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        return id != null && id.equals(((BaseEntity) o).getId());
    }

    @Override
    public final int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
