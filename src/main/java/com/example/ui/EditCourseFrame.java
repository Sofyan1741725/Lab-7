package com.example.ui;

import com.example.models.Course;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;

public class EditCourseFrame extends JFrame {

    private final Course course;
    private final CourseService courseService;
    private final JTextField titleField;
    private final JTextArea descArea;

    public EditCourseFrame(Course course) {
        this.course = course;
        this.courseService = CourseService.getInstance();

        setTitle("Edit Course - " + course.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(8,8));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(root);

        titleField = new JTextField(course.getTitle());
        descArea = new JTextArea(course.getDescription(), 8, 40);

        JPanel form = new JPanel(new GridLayout(2,1,8,8));
        form.add(new CreateCourseFrame.LabeledPanel("Title:", titleField));
        form.add(new CreateCourseFrame.LabeledPanel("Description:", new JScrollPane(descArea)));
        root.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("Save");
        bottom.add(saveBtn);
        root.add(bottom, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveChanges());
    }

    private void saveChanges() {
        String newTitle = titleField.getText().trim();
        String newDesc = descArea.getText().trim();
        if (newTitle.isEmpty() || newDesc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill title and description.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = courseService.updateCourse(course, newTitle, newDesc);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Course updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}