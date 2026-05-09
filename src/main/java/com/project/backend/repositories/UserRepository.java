package com.project.backend.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.backend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Kjo metodë duhet për të gjetur përdoruesin gjatë Login-it
    Optional<User> findByUsername(String username);

    // Kjo metodë duhet për të kontrolluar nëse ekziston username gjatë Register-it
    Boolean existsByUsername(String username);
}