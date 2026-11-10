package com.jewelry.store.entity;

import com.jewelry.store.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private boolean locked = false;

    public String getFullName() {
        return fullName == null ? "" : fullName;
    }

    public UserStatus getStatus() {
        if (locked) {
            return UserStatus.LOCKED;
        }
        if ("GUEST".equalsIgnoreCase(role)) {
            return UserStatus.INACTIVE;
        }
        return UserStatus.ACTIVE;
    }

    public boolean isGuest() {
        return "GUEST".equalsIgnoreCase(role) || "__guest__".equalsIgnoreCase(username);
    }
}
