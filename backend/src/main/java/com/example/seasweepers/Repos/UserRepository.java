package com.example.seasweepers.Repos;

import com.example.seasweepers.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrderByTotalScoreDesc();

    List<User> findAllByOrderByWeeklyPointsDesc();

    // Auth lookups
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}


