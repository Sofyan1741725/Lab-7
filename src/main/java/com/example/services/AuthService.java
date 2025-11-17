package com.example.services;

import com.example.models.Student;
import com.example.models.Instructor;
import com.example.models.User;
import com.example.database.jsonDataBaseManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {

    private final jsonDataBaseManager<User> userDB;

    // Email regex pattern for validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public AuthService(String userJsonPath) {
        this.userDB = new jsonDataBaseManager<>(userJsonPath, User.class, "userId");
    }

    // ================= SHA-256 password hashing =================
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    // ================= Email validation =================
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // ================= Signup =================
    public boolean signupStudent(String userName, String email, String password) {
        if (userName == null || userName.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {
            System.out.println("All fields are required.");
            return false;
        }

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return false;
        }

        // Check if email already exists
        if (userDB.exists(u -> email.equals(u.getEmail()))) {
            System.out.println("Email already registered.");
            return false;
        }

        String hashedPassword = hashPassword(password);
        String uniqueId = userDB.generateUniqueId("stu");

        Student student = new Student();
        student.setUserId(uniqueId.hashCode()); // or use any unique numeric ID scheme
        student.setUserName(userName);
        student.setEmail(email);
        student.setPassword(hashedPassword);

        userDB.add(student);
        System.out.println("Student registered successfully!");
        return true;
    }

    public boolean signupInstructor(String userName, String email, String password) {
        if (userName == null || userName.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {
            System.out.println("All fields are required.");
            return false;
        }

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return false;
        }

        // Check if email already exists
        if (userDB.exists(u -> email.equals(u.getEmail()))) {
            System.out.println("Email already registered.");
            return false;
        }

        String hashedPassword = hashPassword(password);
        String uniqueId = userDB.generateUniqueId("ins");

        Instructor instructor = new Instructor();
        instructor.setUserId(uniqueId.hashCode());
        instructor.setUserName(userName);
        instructor.setEmail(email);
        instructor.setPassword(hashedPassword);

        userDB.add(instructor);
        System.out.println("Instructor registered successfully!");
        return true;
    }

    // ================= Login =================
    public Optional<User> login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Email and password are required.");
            return Optional.empty();
        }

        String hashedPassword = hashPassword(password);

        return userDB.getAll().stream()
                .filter(u -> email.equals(u.getEmail()) && hashedPassword.equals(u.getPassword()))
                .findFirst()
                .map(u -> {
                    System.out.println(u.getUserName() + " logged in successfully!");
                    return u;
                });
    }

    // ================= Logout =================
    public void logout(User user) {
        if (user == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        System.out.println(user.getUserName() + " logged out successfully!");
    }
public class AuthService {
    
}
