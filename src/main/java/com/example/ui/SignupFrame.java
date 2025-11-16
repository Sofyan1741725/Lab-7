package com.example.ui;

import com.example.models.User;
import com.example.services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class SignupFrame extends JFrame {

    private final AuthService authService;
    private final JTextField usernameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleBox;

    public SignupFrame() {
        authService = AuthService.getInstance();

        setTitle("SkillForge - Signup");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(root);

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField();
        form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField();
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField();
        form.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"Student", "Instructor"});
        form.add(roleBox, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back to Login");
        btns.add(createBtn);
        btns.add(backBtn);
        root.add(btns, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> doSignup());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private void doSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String role = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = authService.signup(username, email, pass, role); // expects boolean

        if (!ok) {
            JOptionPane.showMessageDialog(this, "Signup failed. Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Account created. You can login now.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginFrame().setVisible(true);
    }

    private boolean isValidEmail(String email) {
        String re = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(re).matcher(email).matches();
    }
}