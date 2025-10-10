package com.chat_project.Chatty.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllByStatus(Status status);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
