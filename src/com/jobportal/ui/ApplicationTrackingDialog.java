package com.jobportal.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jobportal.dao.ApplicationDAO;
import com.jobportal.model.Application;

public class ApplicationTrackingDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final int userId;
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private DefaultTableModel model;
    private JTable table;

    public ApplicationTrackingDialog(Frame owner, int userId) {
        super(owner, "Application Tracking", true);
        this.userId = userId;
        setSize(650, 350);
        setLocationRelativeTo(owner);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[] { "Application ID", "Job ID", "Job Title", "Status" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete Selected Application");
        deleteBtn.addActionListener(e -> deleteSelectedApplication());
        refreshBtn.addActionListener(e -> loadData());
        bottom.add(deleteBtn);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        List<Application> apps = applicationDAO.getApplicationsByUser(userId);
        model.setRowCount(0);
        for (Application a : apps) {
            model.addRow(new Object[] { a.getId(), a.getJobId(), a.getJobTitle(), a.getStatus() });
        }
    }

    private void deleteSelectedApplication() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application first.");
            return;
        }

        int appId = (Integer) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this application?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (applicationDAO.deleteApplicationByUser(appId, userId)) {
            JOptionPane.showMessageDialog(this, "Application deleted.");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete application.");
        }
    }
}
