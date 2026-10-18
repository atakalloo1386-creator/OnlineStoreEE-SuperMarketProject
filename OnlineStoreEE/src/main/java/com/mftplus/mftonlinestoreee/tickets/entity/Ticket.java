package com.mftplus.mftonlinestoreee.tickets.entity;

import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;
import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tickets")
@SQLDelete(sql="update tickets set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class Ticket extends BaseEntity {

    @Column(name = "subject", nullable = false, length = 150)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;
}
