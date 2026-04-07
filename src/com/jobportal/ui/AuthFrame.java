package com.jobportal.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jobportal.dao.UserDAO;
import com.jobportal.model.User;

public class AuthFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private final UserDAO userDAO = new UserDAO();

    public AuthFrame() {
        setTitle("Job Portal - Login");
        setSize(420, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        emailField = new JTextField();
        passwordField = new JPasswordField();
        roleCombo = new JComboBox<>(new String[] { "JOB_SEEKER", "RECRUITER" });

        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Role:"));
        form.add(roleCombo);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        form.add(loginBtn);
        form.add(registerBtn);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> new RegisterDialog(this).setVisible(true));

        add(form, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password are required.");
            return;
        }

        User user = userDAO.login(email, password, role);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
            return;
        }

        dispose();
        if ("JOB_SEEKER".equals(user.getRole())) {
            new JobSeekerDashboardFrame(user).setVisible(true);
        } else {
            new RecruiterDashboardFrame(user).setVisible(true);
        }
    }
}
