package com.demo.chatApp.user;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
public class AppUser {
    @Id
    private String id;

    @Column(length = 2048)
    private String nickName;

    @Column(length = 2048)
    private String fullName;
    private Status status;
//    private long keyId;

    public AppUser() {
        this.id = UUID.randomUUID().toString();
    }

    public AppUser(String nickName, String fullName, Status status) {
        this.id = UUID.randomUUID().toString();
        this.nickName = nickName;
        this.fullName = fullName;
        this.status = status;

    }

    public AppUser(String id, String nickName, String fullName, Status status) {
        this.id = id;
        this.nickName = nickName;
        this.fullName = fullName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
