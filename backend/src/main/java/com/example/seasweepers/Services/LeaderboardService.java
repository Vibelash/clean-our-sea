package com.example.seasweepers.Services;

import com.example.seasweepers.Models.User;
import com.example.seasweepers.Repos.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class LeaderboardService {

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   public List<User> getLeaderboard(String country) {

    List<User> users = userRepository.findAllByOrderByTotalScoreDesc();

    if (country == null || country.equalsIgnoreCase("all")) {
        return users;
    }

    return users.stream()
            .filter(user -> user.getCountry() != null && user.getCountry().equalsIgnoreCase(country))
            .toList();
}

    public List<User> getWeeklyLeaderboard(String country) {
    List<User> users = userRepository.findAllByOrderByWeeklyPointsDesc();
    if (country == null || country.equalsIgnoreCase("all")) {
        return users;
    }
    return users.stream()
            .filter(user -> user.getCountry() != null && user.getCountry().equalsIgnoreCase(country))
            .toList();
}

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User updateWeeklyGoal(Long userId, int weeklyGoal) {
        if (weeklyGoal <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weekly goal must be greater than 0");
        }
        User user = getUser(userId);
        user.setWeeklyGoal(weeklyGoal);
        return userRepository.save(user);
    }
}
