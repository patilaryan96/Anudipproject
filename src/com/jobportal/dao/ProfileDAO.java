package com.jobportal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jobportal.config.DBConnection;
import com.jobportal.model.Profile;

public class ProfileDAO {

    public Profile getProfileByUserId(int userId) {
        String sql = "SELECT user_id, skills, experience_years, headline FROM profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Profile(
                            rs.getInt("user_id"),
                            rs.getString("skills"),
                            rs.getInt("experience_years"),
                            rs.getString("headline"));
                }
            }
        } catch (SQLException e) {
            System.err.println("getProfileByUserId error: " + e.getMessage());
        }
        return null;
    }

    public boolean saveOrUpdateProfile(Profile profile) {
        String sql = "INSERT INTO profiles(user_id, skills, experience_years, headline) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE skills = VALUES(skills), experience_years = VALUES(experience_years), "
                + "headline = VALUES(headline)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getSkills());
            ps.setInt(3, profile.getExperienceYears());
            ps.setString(4, profile.getHeadline());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("saveOrUpdateProfile error: " + e.getMessage());
            return false;
        }
    }
}
