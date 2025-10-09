package com.studyspace.utils;

import com.studyspace.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Test class to demonstrate and verify streak functionality
 */
public class StreakTest {
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "password123");
    }
    
    @Test
    void testFirstTimeLogin() {
        // First time login should start streak at 1
        testUser.updateStreakOnLogin();
        assertEquals(1, testUser.getCurrentStreak());
    }
    
    @Test
    void testConsecutiveDayLogin() {
        // Simulate user logging in yesterday
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        testUser.setLastLoginAt(yesterday);
        testUser.setCurrentStreak(5); // User had 5 day streak
        
        // User logs in today - should increment streak
        testUser.updateStreakOnLogin();
        assertEquals(6, testUser.getCurrentStreak());
    }
    
    @Test
    void testMissedDayLogin() {
        // Simulate user last logged in 2 days ago
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        testUser.setLastLoginAt(twoDaysAgo);
        testUser.setCurrentStreak(10); // User had 10 day streak
        
        // User logs in today after missing a day - should reset streak
        testUser.updateStreakOnLogin();
        assertEquals(1, testUser.getCurrentStreak());
    }
    
    @Test
    void testSameDayMultipleLogins() {
        // Simulate user already logged in today
        LocalDateTime today = LocalDateTime.now();
        testUser.setLastLoginAt(today);
        testUser.setCurrentStreak(3);
        
        // User logs in again today - streak should not change
        testUser.updateStreakOnLogin();
        assertEquals(3, testUser.getCurrentStreak());
    }
    
    @Test
    void testWasActiveYesterday() {
        // Test wasActiveYesterday method
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        testUser.setLastLoginAt(yesterday);
        
        assertTrue(testUser.wasActiveYesterday());
        
        // Test with different dates
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        testUser.setLastLoginAt(twoDaysAgo);
        
        assertFalse(testUser.wasActiveYesterday());
    }
    
    @Test
    void testDaysSinceLastLogin() {
        // Test getDaysSinceLastLogin method
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        testUser.setLastLoginAt(threeDaysAgo);
        
        assertEquals(3, testUser.getDaysSinceLastLogin());
        
        // Test with null last login
        testUser.setLastLoginAt(null);
        assertEquals(Long.MAX_VALUE, testUser.getDaysSinceLastLogin());
    }
    
    @Test
    void testStreakScenario() {
        // Simulate a realistic streak scenario
        System.out.println("\n=== Streak Test Scenario ===");
        
        // Day 1: First login
        testUser.updateStreakOnLogin();
        testUser.updateLastLogin();
        System.out.println("Day 1: First login - Streak: " + testUser.getCurrentStreak());
        assertEquals(1, testUser.getCurrentStreak());
        
        // Day 2: Consecutive login
        testUser.setLastLoginAt(LocalDateTime.now().minusDays(1));
        testUser.updateStreakOnLogin();
        testUser.updateLastLogin();
        System.out.println("Day 2: Consecutive login - Streak: " + testUser.getCurrentStreak());
        assertEquals(2, testUser.getCurrentStreak());
        
        // Day 3: Consecutive login
        testUser.setLastLoginAt(LocalDateTime.now().minusDays(1));
        testUser.updateStreakOnLogin();
        testUser.updateLastLogin();
        System.out.println("Day 3: Consecutive login - Streak: " + testUser.getCurrentStreak());
        assertEquals(3, testUser.getCurrentStreak());
        
        // Day 5: Missed day 4, streak resets
        testUser.setLastLoginAt(LocalDateTime.now().minusDays(2)); // Last login was 2 days ago
        testUser.updateStreakOnLogin();
        testUser.updateLastLogin();
        System.out.println("Day 5: After missing day 4 - Streak: " + testUser.getCurrentStreak());
        assertEquals(1, testUser.getCurrentStreak());
        
        System.out.println("=== Streak Test Complete ===\n");
    }
}
