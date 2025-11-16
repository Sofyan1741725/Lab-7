package com.example.ui;

import com.example.models.Course;
import com.example.models.Lesson;
import com.example.models.Student;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LessonListFrame extends JFrame {

    private final Student student;
    private final Course course;
    private final CourseService courseService;
    private JList<String> lessonList;
    private List<Lesson> lessons;

    public LessonListFrame(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.courseService = CourseService.getInstance();

        setTitle("Lessons - " + course.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(root);

        lessons = courseService.getLessons(course);
        lessonList = new JList<>(lessons.stream().map(Lesson::getTitle).toArray(String[]::new));
        root.add(new JScrollPane(lessonList), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton openBtn = new JButton("Open Lesson");
        JButton markProgressBtn = new JButton("Mark Completed (selected)");
        controls.add(openBtn);
        controls.add(markProgressBtn);
        root.add(controls, BorderLayout.SOUTH);

        openBtn.addActionListener(e -> openLesson());
        markProgressBtn.addActionListener(e -> markSelectedCompleted());
    }

    private void openLesson() {
        int idx = lessonList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a lesson.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Lesson lesson = lessons.get(idx);
        new LessonViewerFrame(lesson, student, course).setVisible(true);
    }

    private void markSelectedCompleted() {
        int idx = lessonList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a lesson to mark completed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Lesson lesson = lessons.get(idx);
        boolean ok = courseService.markLessonCompleted(student, course, lesson);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Marking as completed failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Marked completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}