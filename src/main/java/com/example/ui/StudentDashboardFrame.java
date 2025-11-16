package com.example.ui;

import com.example.models.*;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboardFrame extends JFrame {

    private final Student student;
    private final CourseService courseService;
    private JList<String> availableList;
    private JList<String> enrolledList;
    private List<Course> availableCourses;
    private List<Course> enrolledCourses;

    public StudentDashboardFrame(Student student) {
        this.student = student;
        this.courseService = CourseService.getInstance();

        setTitle("Student Dashboard - " + student.getUserName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        // Root layout
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(root);

        // Top: controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        top.add(new JLabel("Logged in as: " + student.getUserName()));
        top.add(Box.createHorizontalStrut(20));
        top.add(logoutBtn);
        root.add(top, BorderLayout.NORTH);

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // Center: two lists side by side
        JPanel center = new JPanel(new GridLayout(1,2,10,10));

        // left - available
        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Available Courses"), BorderLayout.NORTH);
        availableCourses = courseService.getAllCourses();
        availableList = new JList<>(availableCourses.stream().map(Course::getTitle).toArray(String[]::new));
        left.add(new JScrollPane(availableList), BorderLayout.CENTER);

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton enrollBtn = new JButton("Enroll");
        JButton detailsBtn = new JButton("View Details");
        leftBtns.add(enrollBtn);
        leftBtns.add(detailsBtn);
        left.add(leftBtns, BorderLayout.SOUTH);

        // right - enrolled
        JPanel right = new JPanel(new BorderLayout(6,6));
        right.add(new JLabel("Your Courses"), BorderLayout.NORTH);
        enrolledCourses = courseService.getEnrolledCourses(student);
        enrolledList = new JList<>(enrolledCourses.stream().map(Course::getTitle).toArray(String[]::new));
        right.add(new JScrollPane(enrolledList), BorderLayout.CENTER);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton openLessonsBtn = new JButton("Open Lessons");
        JButton courseProgressBtn = new JButton("View Progress");
        rightBtns.add(openLessonsBtn);
        rightBtns.add(courseProgressBtn);
        right.add(rightBtns, BorderLayout.SOUTH);

        center.add(left);
        center.add(right);
        root.add(center, BorderLayout.CENTER);

        // actions
        enrollBtn.addActionListener(e -> doEnroll());
        detailsBtn.addActionListener(e -> viewCourseDetailsFromAvailable());
        openLessonsBtn.addActionListener(e -> openLessons());
        courseProgressBtn.addActionListener(e -> showProgress());

        // refresh window when regaining focus (simple)
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                refreshData();
            }
        });
    }

    private void refreshData() {
        availableCourses = courseService.getAllCourses();
        availableList.setListData(availableCourses.stream().map(Course::getTitle).toArray(String[]::new));
        enrolledCourses = courseService.getEnrolledCourses(student);
        enrolledList.setListData(enrolledCourses.stream().map(Course::getTitle).toArray(String[]::new));
    }

    private void doEnroll() {
        int idx = availableList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Choose a course to enroll.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = availableCourses.get(idx);
        boolean ok = courseService.enrollStudent(student, c);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Enrollment failed or already enrolled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Enrolled in " + c.getTitle(), "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshData();
    }

    private void openLessons() {
        int idx = enrolledList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select your enrolled course.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = enrolledCourses.get(idx);
        new LessonListFrame(student, c).setVisible(true);
    }

    private void viewCourseDetailsFromAvailable() {
        int idx = availableList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = availableCourses.get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(c.getTitle()).append("\n\n");
        sb.append("Description: ").append(c.getDescription()).append("\n\n");
        sb.append("Instructor: ").append(c.getInstructorId()).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "Course Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProgress() {
        int idx = enrolledList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select your enrolled course.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = enrolledCourses.get(idx);
        String progress = courseService.getProgressSummary(student, c); 
        JOptionPane.showMessageDialog(this, progress, "Progress", JOptionPane.INFORMATION_MESSAGE);
    }
}