package com.eduaircontrol.backend.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "google_id", nullable = true, unique = true)
    private String googleId;
    @Column(name = "company_code", nullable = true)
    private String companyCode;
    @Enumerated(EnumType.STRING)
    private Role role;
}
