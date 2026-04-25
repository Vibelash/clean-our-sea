package com.example.seasweepers.Controllers;

import com.example.seasweepers.Models.User;
import com.example.seasweepers.Services.LeaderboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
public List<User> getLeaderboard(@RequestParam(required = false) String country) {
    return leaderboardService.getLeaderboard(country);
}
    @GetMapping("/weekly")
 public List<User> getWeeklyLeaderboard(@RequestParam(required = false) String country) {
    return leaderboardService.getWeeklyLeaderboard(country);
     
   

    @GetMapping("/{userId}/weekly-goal")
    public WeeklyGoalResponse getWeeklyGoal(@PathVariable Long userId) {
        User user = leaderboardService.getUser(userId);
        return WeeklyGoalResponse.fromUser(user);
    }

    @PutMapping("/{userId}/weekly-goal")
    public WeeklyGoalResponse setWeeklyGoal(@PathVariable Long userId,
                                            @RequestBody WeeklyGoalRequest request) {
        User user = leaderboardService.updateWeeklyGoal(userId, request.getWeeklyGoal());
        return WeeklyGoalResponse.fromUser(user);
    }

    public static class WeeklyGoalRequest {
        private int weeklyGoal;

        public int getWeeklyGoal() {
            return weeklyGoal;
        }

        public void setWeeklyGoal(int weeklyGoal) {
            this.weeklyGoal = weeklyGoal;
        }
    }

    public static class WeeklyGoalResponse {
        private int weeklyGoal;
        private int weeklyPoints;
        private int progressPercent;

        public static WeeklyGoalResponse fromUser(User user) {
            WeeklyGoalResponse response = new WeeklyGoalResponse();
            response.weeklyGoal = user.getWeeklyGoal();
            response.weeklyPoints = user.getWeeklyPoints();
            response.progressPercent = calculateProgressPercent(user.getWeeklyGoal(), user.getWeeklyPoints());
            return response;
        }

        private static int calculateProgressPercent(int weeklyGoal, int weeklyPoints) {
            if (weeklyGoal <= 0) {
                return 0;
            }
            int percent = (int) Math.round((weeklyPoints * 100.0) / weeklyGoal);
            return Math.min(100, percent);
        }

        public int getWeeklyGoal() {
            return weeklyGoal;
        }

        public int getWeeklyPoints() {
            return weeklyPoints;
        }

        public int getProgressPercent() {
            return progressPercent;
        }
    }
}
