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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.jobportal.dao.ApplicationDAO;
import com.jobportal.dao.JobDAO;
import com.jobportal.model.Job;
import com.jobportal.model.User;

public class JobSeekerDashboardFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final User user;
    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private JTable jobTable;
    private DefaultTableModel jobModel;
    private JTextField searchField;

    public JobSeekerDashboardFrame(User user) {
        this.user = user;
        setTitle("Job Seeker Dashboard - " + user.getName());
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        loadJobs(jobDAO.getAllJobs());
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(new JLabel("Welcome, " + user.getName()), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        JButton allBtn = new JButton("Show All");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(allBtn);
        top.add(searchPanel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        String[] cols = { "Job ID", "Title", "Skills", "Salary", "Description" };
        jobModel = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jobTable = new JTable(jobModel);
        jobTable.setRowHeight(28);
        jobTable.getColumnModel().getColumn(4).setPreferredWidth(320);
        add(new JScrollPane(jobTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 4, 8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton applyBtn = new JButton("Apply");
        JButton profileBtn = new JButton("My Profile");
        JButton trackBtn = new JButton("Track Applications");
        JButton logoutBtn = new JButton("Logout");
        bottom.add(applyBtn);
        bottom.add(profileBtn);
        bottom.add(trackBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchJobs());
        allBtn.addActionListener(e -> loadJobs(jobDAO.getAllJobs()));
        applyBtn.addActionListener(e -> applyForSelectedJob());
        profileBtn.addActionListener(e -> new ProfileDialog(this, user.getId()).setVisible(true));
        trackBtn.addActionListener(e -> new ApplicationTrackingDialog(this, user.getId()).setVisible(true));
        logoutBtn.addActionListener(e -> logout());
    }

    private void searchJobs() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter title or skill to search.");
            return;
        }
        loadJobs(jobDAO.searchJobs(keyword));
    }

    private void loadJobs(List<Job> jobs) {
        jobModel.setRowCount(0);
        for (Job j : jobs) {
            String salaryText = String.format("%.2f %s", j.getSalary(), j.getSalaryType());
            jobModel.addRow(new Object[] { j.getId(), j.getTitle(), j.getSkills(), salaryText, j.getDescription() });
        }
    }

    private void applyForSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job first.");
            return;
        }
        int jobId = (Integer) jobModel.getValueAt(row, 0);
        if (applicationDAO.applyForJob(jobId, user.getId())) {
            JOptionPane.showMessageDialog(this, "Application submitted.");
        } else {
            JOptionPane.showMessageDialog(this, "You may have already applied, or apply failed.");
        }
    }

    private void logout() {
        dispose();
        new AuthFrame().setVisible(true);
    }
}
