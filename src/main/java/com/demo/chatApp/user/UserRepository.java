package com.demo.chatApp.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository  extends JpaRepository<AppUser, String> {
    List<AppUser> findAllByStatus(Status status);
}