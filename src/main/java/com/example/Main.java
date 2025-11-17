package com.example;

import com.example.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // تشغيل واجهة المستخدم في الـ Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // نبدأ البرنامج بشاشة تسجيل الدخول
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}