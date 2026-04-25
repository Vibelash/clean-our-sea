package com.example.seasweepers.Repos;

import com.example.seasweepers.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrderByTotalScoreDesc();
    List<User> findByCountryIgnoreCaseOrderByTotalScoreDesc(String country);
    List<User> findAllByOrderByWeeklyPointsDesc();

}


