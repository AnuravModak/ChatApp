package com.demo.chatApp.user;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {


    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public AppUser saveUser(AppUser user) {
        if (user.getId() == null || !repository.existsById(user.getId())) {
            // Generate a new ID if it's null or the user doesn't exist
            user.setId(UUID.randomUUID().toString());
        }
        return repository.save(user);
    }


    public void disconnect(AppUser user) {
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User or Nickname cannot be null");
        }

        repository.findById( user.getUsername() ).ifPresent(storedUser -> {
            storedUser.setStatus(Status.OFFLINE);
            repository.save(storedUser);
        });
    }

    public List<AppUser> findConnectedUsers() {
        return repository.findAllByStatus(Status.ONLINE);
    }
}
