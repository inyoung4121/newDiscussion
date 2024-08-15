package com.example.demo.user.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String name;

    private String profile;
    private String intro;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(String email, String password, String name, String profile, String intro) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profile = profile;
        this.intro = intro;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void nameUpdate(String newName){
        this.name = newName;
    }
    public void profileUpdate(String newpPofile){
        this.profile = newpPofile;
    }
    public void introUpdate(String newIntro){
        this.intro = newIntro;
    }
    public void passwordUpdate(String newPassword){
        this.password = newPassword;
    }

    public User orElseThrow(Object invalidFollowingId) {
        return null;
    }
}
