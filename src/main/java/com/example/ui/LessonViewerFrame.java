package com.example.ui;

import com.example.models.Course;
import com.example.models.Lesson;
import com.example.models.Student;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;

public class LessonViewerFrame extends JFrame {

    private final Lesson lesson;
    private final Student student; // nullable if opened by instructor
    private final Course course;
    private final CourseService courseService;

    // Constructor for simple viewer used by instructor or student via LessonListFrame.openLesson()
    public LessonViewerFrame(Lesson lesson) {
        this(lesson, null, null);
    }

    // Preferred constructor used by students to allow marking completion
    public LessonViewerFrame(Lesson lesson, Student student, Course course) {
        this.lesson = lesson;
        this.student = student;
        this.course = course;
        this.courseService = CourseService.getInstance();

        setTitle("Lesson - " + lesson.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 560);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(root);

        JTextArea contentArea = new JTextArea(lesson.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        JScrollPane sp = new JScrollPane(contentArea);
        root.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton markBtn = new JButton("Mark Completed");
        JButton closeBtn = new JButton("Close");
        bottom.add(markBtn);
        bottom.add(closeBtn);
        root.add(bottom, BorderLayout.SOUTH);

        markBtn.addActionListener(e -> markCompleted());
        closeBtn.addActionListener(e -> dispose());

        if (student == null || course == null) {
            markBtn.setEnabled(false);
        }
    }

    private void markCompleted() {
        if (student == null || course == null) return;
        boolean ok = courseService.markLessonCompleted(student, course, lesson);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to mark completed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Lesson marked completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}