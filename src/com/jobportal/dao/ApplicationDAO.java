package com.jobportal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jobportal.config.DBConnection;
import com.jobportal.model.Application;

public class ApplicationDAO {
    public static final String APPLIED = "Applied";
    public static final String REVIEWED = "Reviewed";
    public static final String SELECTED = "Selected";
    public static final String REJECTED = "Rejected";

    public boolean applyForJob(int jobId, int userId) {
        if (hasAlreadyApplied(jobId, userId)) {
            return false;
        }
        String sql = "INSERT INTO applications(job_id, user_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, userId);
            ps.setString(3, APPLIED);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("applyForJob error: " + e.getMessage());
            return false;
        }
    }

    public boolean hasAlreadyApplied(int jobId, int userId) {
        String sql = "SELECT id FROM applications WHERE job_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("hasAlreadyApplied error: " + e.getMessage());
            return false;
        }
    }

    public List<Application> getApplicationsByUser(int userId) {
        String sql = "SELECT a.id, a.job_id, a.user_id, a.status, j.title "
                + "FROM applications a JOIN jobs j ON a.job_id = j.id WHERE a.user_id = ? ORDER BY a.id DESC";
        List<Application> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application app = new Application(
                            rs.getInt("id"),
                            rs.getInt("job_id"),
                            rs.getInt("user_id"),
                            rs.getString("status"));
                    app.setJobTitle(rs.getString("title"));
                    list.add(app);
                }
            }
        } catch (SQLException e) {
            System.err.println("getApplicationsByUser error: " + e.getMessage());
        }
        return list;
    }

    public List<Application> getApplicantsByJob(int jobId) {
        String sql = "SELECT id, job_id, user_id, status FROM applications WHERE job_id = ? ORDER BY id DESC";
        List<Application> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Application(
                            rs.getInt("id"),
                            rs.getInt("job_id"),
                            rs.getInt("user_id"),
                            rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            System.err.println("getApplicantsByJob error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateApplicationStatus(int applicationId, String status) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateApplicationStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteApplicationByUser(int applicationId, int userId) {
        String sql = "DELETE FROM applications WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteApplicationByUser error: " + e.getMessage());
            return false;
        }
    }
}
