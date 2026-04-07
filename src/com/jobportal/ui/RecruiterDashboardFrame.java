package com.jobportal.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jobportal.dao.JobDAO;
import com.jobportal.model.Job;
import com.jobportal.model.User;

public class RecruiterDashboardFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final User recruiter;
    private final JobDAO jobDAO = new JobDAO();
    private JTable jobsTable;
    private DefaultTableModel jobsModel;

    public RecruiterDashboardFrame(User recruiter) {
        this.recruiter = recruiter;
        setTitle("Recruiter Dashboard - " + recruiter.getName());
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        loadJobs();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(new JLabel("Welcome, " + recruiter.getName()), BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        String[] cols = { "Job ID", "Title", "Skills", "Salary", "Description" };
        jobsModel = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jobsTable = new JTable(jobsModel);
        jobsTable.setRowHeight(28);
        jobsTable.getColumnModel().getColumn(4).setPreferredWidth(320);
        add(new JScrollPane(jobsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 5, 8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton postJobBtn = new JButton("Post New Job");
        JButton refreshBtn = new JButton("Refresh");
        JButton viewApplicantsBtn = new JButton("View Applicants");
        JButton deleteJobBtn = new JButton("Delete Job");
        JButton logoutBtn = new JButton("Logout");
        bottom.add(postJobBtn);
        bottom.add(refreshBtn);
        bottom.add(viewApplicantsBtn);
        bottom.add(deleteJobBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);

        postJobBtn.addActionListener(e -> {
            new PostJobDialog(this, recruiter.getId()).setVisible(true);
            loadJobs();
        });
        refreshBtn.addActionListener(e -> loadJobs());
        viewApplicantsBtn.addActionListener(e -> openApplicantsDialog());
        deleteJobBtn.addActionListener(e -> deleteSelectedJob());
        logoutBtn.addActionListener(e -> logout());
    }

    private void loadJobs() {
        List<Job> jobs = jobDAO.getJobsByRecruiter(recruiter.getId());
        jobsModel.setRowCount(0);
        for (Job j : jobs) {
            String salaryText = String.format("%.2f %s", j.getSalary(), j.getSalaryType());
            jobsModel.addRow(new Object[] { j.getId(), j.getTitle(), j.getSkills(), salaryText, j.getDescription() });
        }
    }

    private void openApplicantsDialog() {
        int row = jobsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select one job first.");
            return;
        }
        int jobId = (Integer) jobsModel.getValueAt(row, 0);
        String title = (String) jobsModel.getValueAt(row, 1);
        new ApplicantsDialog(this, jobId, title).setVisible(true);
    }

    private void deleteSelectedJob() {
        int row = jobsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select one job first.");
            return;
        }
        int jobId = (Integer) jobsModel.getValueAt(row, 0);
        String title = (String) jobsModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete job '" + title + "'?\nAll related applications will also be deleted.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (jobDAO.deleteJobByRecruiter(jobId, recruiter.getId())) {
            JOptionPane.showMessageDialog(this, "Job deleted successfully.");
            loadJobs();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete the job.");
        }
    }

    private void logout() {
        dispose();
        new AuthFrame().setVisible(true);
    }
}
