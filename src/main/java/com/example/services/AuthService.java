package com.example.services;

import com.example.models.Student;
import com.example.models.Instructor;
import com.example.models.User;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class AuthService {

    private static AuthService instance;
    private final List<User> users;
    private final Gson gson;

    private final File usersFile = new File("users.json");

    private AuthService() {
        users = new ArrayList<>();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
        loadUsers();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean signup(String username, String email, String password, String role) {
        if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            return false; // email exists
        }

        User u;
        if (role.equalsIgnoreCase("Student")) {
            u = new Student(0, username, email, password);
        } else {
            u = new Instructor(0, username, email, password);
        }

        users.add(u);
        saveUsers();
        return true;
    }

    public User login(String email, String password) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    private void loadUsers() {
        if (!usersFile.exists()) return;

        try (Reader reader = new FileReader(usersFile)) {
            User[] arr = gson.fromJson(reader, User[].class);
            if (arr != null) users.addAll(Arrays.asList(arr));
        } catch (IOException e) {
            System.out.println("Failed to load users: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (Writer writer = new FileWriter(usersFile)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.out.println("Failed to save users: " + e.getMessage());
        }
    }

    // Custom deserializer to handle User -> Student/Instructor
    private static class UserDeserializer implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String role = obj.has("role") ? obj.get("role").getAsString() : "Student"; // default Student
            if (role.equalsIgnoreCase("Instructor")) {
                return context.deserialize(json, Instructor.class);
            } else {
                return context.deserialize(json, Student.class);
            }
        }
    }
}
