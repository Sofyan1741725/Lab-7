package com.example.ui;

import com.example.models.Instructor;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;

public class CreateCourseFrame extends JFrame {

    private final Instructor instructor;
    private final CourseService courseService;

    private final JTextField titleField;
    private final JTextArea descArea;

    public CreateCourseFrame(Instructor instructor) {
        this.instructor = instructor;
        this.courseService = CourseService.getInstance();

        setTitle("Create Course");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(8,8));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(root);

        JPanel form = new JPanel(new GridLayout(2,1,8,8));
        titleField = new JTextField();
        descArea = new JTextArea(8, 40);
        form.add(new LabeledPanel("Title:", titleField));
        form.add(new LabeledPanel("Description:", new JScrollPane(descArea)));

        root.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createBtn = new JButton("Create");
        bottom.add(createBtn);
        root.add(bottom, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> createCourse());
    }

    private void createCourse() {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();
        if (title.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill title and description.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = courseService.createCourse(instructor, title, desc);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Create course failed (duplicate or backend error).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Course created.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    static class LabeledPanel extends JPanel {
        public LabeledPanel(String label, Component comp) {
            setLayout(new BorderLayout(6,6));
            add(new JLabel(label), BorderLayout.WEST);
            add(comp, BorderLayout.CENTER);
        }
    }
}