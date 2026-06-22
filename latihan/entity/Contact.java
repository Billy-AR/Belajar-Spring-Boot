package com.backend.latihan.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "CONTACTS")
@Getter
@Setter
@NamedQueries({
        @NamedQuery(name = "Contact.updateStatusById", query = "UPDATE Contact c SET c.status = :status, c.updatedAt = :updatedAt,c.updatedBy = :updatedBy")
})
public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "message", nullable = false)
    private String message;

    @ColumnDefault("NEW")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "user_type", nullable = false, length = 50)
    private String userType;





}
