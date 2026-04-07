package com.jobportal.ui;

import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jobportal.dao.JobDAO;
import com.jobportal.model.Job;

public class PostJobDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final int recruiterId;
    private final JobDAO jobDAO = new JobDAO();

    private JTextField titleField;
    private JTextArea descArea;
    private JTextField skillsField;
    private JTextField salaryField;
    private JComboBox<String> salaryTypeCombo;

    public PostJobDialog(Frame owner, int recruiterId) {
        super(owner, "Post New Job", true);
        this.recruiterId = recruiterId;
        setSize(600, 420);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleField = new JTextField();
        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        skillsField = new JTextField();
        salaryField = new JTextField();
        salaryTypeCombo = new JComboBox<>(new String[] { "LPA", "Monthly" });

        panel.add(new JLabel("Job Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descArea));
        panel.add(new JLabel("Skills Required:"));
        panel.add(skillsField);
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);
        panel.add(new JLabel("Salary Type:"));
        panel.add(salaryTypeCombo);

        JButton saveBtn = new JButton("Post Job");
        JButton closeBtn = new JButton("Close");
        panel.add(saveBtn);
        panel.add(closeBtn);

        saveBtn.addActionListener(e -> postJob());
        closeBtn.addActionListener(e -> dispose());

        add(panel);
    }

    private void postJob() {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();
        String skills = skillsField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String salaryType = (String) salaryTypeCombo.getSelectedItem();

        if (title.isEmpty() || desc.isEmpty() || skills.isEmpty() || salaryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        double salary;
        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid number.");
            return;
        }

        Job job = new Job(0, recruiterId, title, desc, skills, salary, salaryType);
        if (jobDAO.postJob(job)) {
            JOptionPane.showMessageDialog(this, "Job posted successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Could not post the job.");
        }
    }
}
