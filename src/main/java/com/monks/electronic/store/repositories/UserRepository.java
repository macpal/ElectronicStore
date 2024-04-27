package com.monks.electronic.store.repositories;

import com.monks.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndId(String email, String id);
    List<User> findByNameContaining(String keyword);
}
