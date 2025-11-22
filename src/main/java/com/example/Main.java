package com.example;

import com.example.database.jsonDataBaseManager;
import com.example.database.jsonDataBaseManager;
import com.example.models.User;
import com.example.models.Student;
import com.example.models.Instructor;
import com.example.ui.LoginFrame;   
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.util.function.Function;

public class Main {

    // Shared database managers
    public static jsonDataBaseManager<User> userDb;
    public static jsonDataBaseManager<Object> courseDb;
    
    public static void main(String[] args) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        
        Function<JsonObject, User> userDeserializer = json -> {
            String role = json.has("role") ? json.get("role").getAsString() : "";
            switch (role.toLowerCase()) {
                case "instructor":
                    return gson.fromJson(json, Instructor.class);
                case "student":
                default:
                    return gson.fromJson(json, Student.class);
            }
        };

        
        userDb = new jsonDataBaseManager<>(
                "src/main/resources/users.json",
                User.class,
                "userId",
                gson,
                (Function) userDeserializer
        );

        courseDb = new jsonDataBaseManager<>(
                "src/main/resources/courses.json",
                Object.class,      // will be replaced when your Course class is ready
                "courseId",
                gson,
                null
        );

        
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
