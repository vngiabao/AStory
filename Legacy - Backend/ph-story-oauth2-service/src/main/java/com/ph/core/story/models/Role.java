package com.ph.core.story.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ví dụ: ROLE_ADMIN ROLE_USER
     */
    @Column(nullable = false, length = 50, unique = true)
    private String name;
}
