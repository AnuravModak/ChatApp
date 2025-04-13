package com.demo.chatApp.keyManager;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "master_keys")
public class KeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 2048)
    private String publicKey;

    @Column(length = 4096)
    private String privateKey;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public KeyEntity() {

    }

    public KeyEntity(long id, String publicKey, String privateKey, boolean active, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.active = active;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
