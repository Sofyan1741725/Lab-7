package com.example.ui;

import com.example.models.User;
import com.example.models.Student;
import com.example.models.Instructor;
import com.example.services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final JTextField emailField;
    private final JPasswordField passwordField;

    public LoginFrame() {
        authService = AuthService.getInstance();

        setTitle("SkillForge - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 240);
        setLocationRelativeTo(null);

        // Root panel
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(root);

        // Form panel
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new BorderLayout(6, 6));
        row1.add(new JLabel("Email:"), BorderLayout.WEST);
        emailField = new JTextField();
        row1.add(emailField, BorderLayout.CENTER);
        form.add(row1);
        form.add(Box.createVerticalStrut(8));

        JPanel row2 = new JPanel(new BorderLayout(6, 6));
        row2.add(new JLabel("Password:"), BorderLayout.WEST);
        passwordField = new JPasswordField();
        row2.add(passwordField, BorderLayout.CENTER);
        form.add(row2);
        form.add(Box.createVerticalStrut(12));

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Go to Signup");
        buttons.add(loginBtn);
        buttons.add(signupBtn);
        root.add(buttons, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> doLogin());
        signupBtn.addActionListener(e -> {
            dispose();
            new SignupFrame().setVisible(true);
        });
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill email and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authService.login(email, pass); // expects null on fail

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Auth Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // open appropriate dashboard
        SwingUtilities.invokeLater(() -> {
            dispose();
            if (user instanceof Student) {
                new StudentDashboardFrame((Student) user).setVisible(true);
            } else if (user instanceof Instructor) {
                new InstructorDashboardFrame((Instructor) user).setVisible(true);
            } else {
                // fallback
                JOptionPane.showMessageDialog(null, "Unknown user role.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private boolean isValidEmail(String email) {
        // simple regex; backend should re-validate
        String re = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(re).matcher(email).matches();
    }
}