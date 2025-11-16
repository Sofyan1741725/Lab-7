package com.example.models;

import java.util.ArrayList;

public abstract class User {

    private int userId;
    private String userName;
    private String email;
    private String password;
    private boolean isLoggedIn;
    public static ArrayList<User> users = new ArrayList<>();

    public User() {}

    public User(int userId, String userName, String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.isLoggedIn = false;
        users.add(this);
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static User login(String email, String password) {
        for (User u : users) {
            if (email != null && password != null &&
                    email.equals(u.email) && password.equals(u.password)) {
                u.isLoggedIn = true;
                System.out.println("Logged In Successfully...");
                return u;
            }
        }
        System.out.println("Wrong User Name Or Password");
        return null;
    }

    public void logout() {
        if (isLoggedIn) {
            isLoggedIn = false;
            System.out.println(userName + " Logged Out Successfully..");
        } else {
            System.out.println("User Is Not Logged In");
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }
}
