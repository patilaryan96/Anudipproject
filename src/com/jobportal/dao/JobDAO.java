package com.jobportal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jobportal.config.DBConnection;
import com.jobportal.model.Job;

public class JobDAO {

    public boolean postJob(Job job) {
        String sql = "INSERT INTO jobs(recruiter_id, title, description, salary, salary_type, skills) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getRecruiterId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setDouble(4, job.getSalary());
            ps.setString(5, job.getSalaryType());
            ps.setString(6, job.getSkills());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("postJob error: " + e.getMessage());
            return false;
        }
    }

    public List<Job> getAllJobs() {
        String sql = "SELECT id, recruiter_id, title, description, salary, salary_type, skills FROM jobs ORDER BY id DESC";
        return fetchJobs(sql, null);
    }

    public List<Job> searchJobs(String keyword) {
        String sql = "SELECT id, recruiter_id, title, description, salary, salary_type, skills FROM jobs "
                + "WHERE title LIKE ? OR skills LIKE ? ORDER BY id DESC";
        List<Job> jobs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("searchJobs error: " + e.getMessage());
        }
        return jobs;
    }

    public List<Job> getJobsByRecruiter(int recruiterId) {
        String sql = "SELECT id, recruiter_id, title, description, salary, salary_type, skills FROM jobs WHERE recruiter_id = ? ORDER BY id DESC";
        return fetchJobs(sql, recruiterId);
    }

    public boolean deleteJobByRecruiter(int jobId, int recruiterId) {
        String sql = "DELETE FROM jobs WHERE id = ? AND recruiter_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, recruiterId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteJobByRecruiter error: " + e.getMessage());
            return false;
        }
    }

    private List<Job> fetchJobs(String sql, Integer recruiterId) {
        List<Job> jobs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            if (recruiterId != null) {
                ps.setInt(1, recruiterId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("fetchJobs error: " + e.getMessage());
        }
        return jobs;
    }

    private Job mapRow(ResultSet rs) throws SQLException {
        return new Job(
                rs.getInt("id"),
                rs.getInt("recruiter_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("skills"),
                rs.getDouble("salary"),
                rs.getString("salary_type"));
    }
}
