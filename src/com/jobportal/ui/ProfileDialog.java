package com.jobportal.ui;

import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jobportal.dao.ProfileDAO;
import com.jobportal.model.Profile;

public class ProfileDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final int userId;
    private final ProfileDAO profileDAO = new ProfileDAO();
    private JTextArea skillsArea;
    private JTextField expField;
    private JTextArea headlineArea;

    public ProfileDialog(Frame owner, int userId) {
        super(owner, "My Profile", true);
        this.userId = userId;
        setSize(560, 360);
        setLocationRelativeTo(owner);
        initUI();
        loadProfile();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        skillsArea = new JTextArea(4, 20);
        skillsArea.setLineWrap(true);
        skillsArea.setWrapStyleWord(true);
        expField = new JTextField();
        headlineArea = new JTextArea(3, 20);
        headlineArea.setLineWrap(true);
        headlineArea.setWrapStyleWord(true);

        panel.add(new JLabel("Skills (comma separated):"));
        panel.add(new JScrollPane(skillsArea));
        panel.add(new JLabel("Experience (years):"));
        panel.add(expField);
        panel.add(new JLabel("Profile Headline:"));
        panel.add(new JScrollPane(headlineArea));

        JButton saveBtn = new JButton("Save");
        JButton closeBtn = new JButton("Close");
        panel.add(saveBtn);
        panel.add(closeBtn);

        saveBtn.addActionListener(e -> saveProfile());
        closeBtn.addActionListener(e -> dispose());

        add(panel);
    }

    private void loadProfile() {
        Profile p = profileDAO.getProfileByUserId(userId);
        if (p != null) {
            skillsArea.setText(p.getSkills());
            expField.setText(String.valueOf(p.getExperienceYears()));
            headlineArea.setText(p.getHeadline());
        }
    }

    private void saveProfile() {
        String skills = skillsArea.getText().trim();
        String expText = expField.getText().trim();
        String headline = headlineArea.getText().trim();

        if (skills.isEmpty() || expText.isEmpty() || headline.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All profile fields are required.");
            return;
        }

        int expYears;
        try {
            expYears = Integer.parseInt(expText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Experience must be a number.");
            return;
        }

        Profile profile = new Profile(userId, skills, expYears, headline);
        if (profileDAO.saveOrUpdateProfile(profile)) {
            JOptionPane.showMessageDialog(this, "Profile saved successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Could not save profile.");
        }
    }
}
