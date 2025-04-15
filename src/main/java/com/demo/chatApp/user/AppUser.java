package com.demo.chatApp.user;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
public class AppUser {
    @Id
    private String id;

    @Column(length = 2048)
    private String Username;

    @Column(length = 2048)
    private String firstName;
    private Status status;

    public AppUser() {
        this.id = UUID.randomUUID().toString();
    }

    public AppUser(String id, String username, String firstName, Status status) {
        this.id = id;
        Username = username;
        this.firstName = firstName;
        this.status = status;
    }

    public AppUser(String username, String firstName, Status status) {
        this.id = UUID.randomUUID().toString();
        Username = username;
        this.firstName = firstName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
