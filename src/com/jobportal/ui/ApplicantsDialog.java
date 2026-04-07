package com.jobportal.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jobportal.dao.ApplicationDAO;
import com.jobportal.model.Application;

public class ApplicantsDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final int jobId;
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<String> statusCombo;

    public ApplicantsDialog(Frame owner, int jobId, String jobTitle) {
        super(owner, "Applicants - " + jobTitle, true);
        this.jobId = jobId;
        setSize(700, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[] { "Application ID", "User ID", "Status" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        statusCombo = new JComboBox<>(new String[] {
                ApplicationDAO.APPLIED,
                ApplicationDAO.REVIEWED,
                ApplicationDAO.SELECTED,
                ApplicationDAO.REJECTED });
        JButton updateBtn = new JButton("Update Status");
        JButton refreshBtn = new JButton("Refresh");
        bottom.add(statusCombo);
        bottom.add(updateBtn);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        updateBtn.addActionListener(e -> updateStatus());
        refreshBtn.addActionListener(e -> loadApplicants());

        loadApplicants();
    }

    private void loadApplicants() {
        List<Application> list = applicationDAO.getApplicantsByJob(jobId);
        model.setRowCount(0);
        for (Application app : list) {
            model.addRow(new Object[] { app.getId(), app.getUserId(), app.getStatus() });
        }
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an applicant first.");
            return;
        }
        int appId = (Integer) model.getValueAt(row, 0);
        String status = (String) statusCombo.getSelectedItem();

        if (applicationDAO.updateApplicationStatus(appId, status)) {
            JOptionPane.showMessageDialog(this, "Status updated.");
            loadApplicants();
        } else {
            JOptionPane.showMessageDialog(this, "Could not update status.");
        }
    }
}
