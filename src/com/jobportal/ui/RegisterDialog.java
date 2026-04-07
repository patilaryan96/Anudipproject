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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jobportal.dao.UserDAO;
import com.jobportal.model.User;

public class RegisterDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private final UserDAO userDAO = new UserDAO();

    public RegisterDialog(Frame owner) {
        super(owner, "Register", true);
        setSize(420, 280);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        roleCombo = new JComboBox<>(new String[] { "JOB_SEEKER", "RECRUITER" });

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        JButton saveBtn = new JButton("Create Account");
        JButton cancelBtn = new JButton("Cancel");
        panel.add(saveBtn);
        panel.add(cancelBtn);

        saveBtn.addActionListener(e -> register());
        cancelBtn.addActionListener(e -> dispose());

        add(panel);
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email.");
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
            return;
        }
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already exists.");
            return;
        }

        User user = new User(0, name, email, password, role);
        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful. Please login.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed.");
        }
    }
}
