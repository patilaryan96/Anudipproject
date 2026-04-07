package com.jobportal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jobportal.ui.AuthFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Look and Feel error: " + e.getMessage());
            }
            new AuthFrame().setVisible(true);
        });
    }
}
