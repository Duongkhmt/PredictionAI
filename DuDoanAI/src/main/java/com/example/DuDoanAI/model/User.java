
package com.example.DuDoanAI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank(message = "Tên người dùng không được để trống")
//    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3 đến 50 ký tự")
//    @Column(unique = true, nullable = false)
//    private String username;
    @NotBlank(message = "Gmail không được để trống")
    @Email(message = "Gmail không hợp lệ")
    @Column(unique = true, nullable = false)
    private String username; // chính là Gmail

    // Raw password chỉ dùng khi đăng ký local, không lưu DB
    @Transient
    private String rawPassword;

    // Password hash để lưu DB (có thể null nếu user đăng nhập Google)
    @Column
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private boolean enabled;

    // Roles: USER, ADMIN
    private String role;

    // Provider: LOCAL, GOOGLE
    @Column(nullable = false)
    private String provider;

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
        this.role = "USER";
        this.provider = "LOCAL"; // mặc định
    }

    public User(String username, String rawPassword) {
        this();
        this.username = username;
        this.rawPassword = rawPassword;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getRawPassword() { return rawPassword; }
    public void setRawPassword(String rawPassword) { this.rawPassword = rawPassword; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", enabled=" + enabled +
                ", role='" + role + '\'' +
                ", provider='" + provider + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
