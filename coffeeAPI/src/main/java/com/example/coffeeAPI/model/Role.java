package com.example.coffeeAPI.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private RoleName name;

    @Override
    public String getAuthority() {
        return name.name();
    }

    public enum RoleName{
        ROLE_USER,
        ROLE_ADMIN
    }
}
