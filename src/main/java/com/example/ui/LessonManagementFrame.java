package com.example.ui;

import com.example.models.Course;
import com.example.models.Lesson;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LessonManagementFrame extends JFrame {

    private final Course course;
    private final CourseService courseService;
    private JList<String> lessonList;
    private List<Lesson> lessons;

    public LessonManagementFrame(Course course) {
        this.course = course;
        this.courseService = CourseService.getInstance();

        setTitle("Manage Lessons - " + course.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 480);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(root);

        lessons = courseService.getLessons(course);
        lessonList = new JList<>(lessons.stream().map(Lesson::getTitle).toArray(String[]::new));
        root.add(new JScrollPane(lessonList), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        JButton addBtn = new JButton("Add Lesson");
        JButton editBtn = new JButton("Edit Lesson");
        JButton delBtn = new JButton("Delete Lesson");
        controls.add(addBtn);
        controls.add(Box.createVerticalStrut(8));
        controls.add(editBtn);
        controls.add(Box.createVerticalStrut(8));
        controls.add(delBtn);

        root.add(controls, BorderLayout.EAST);

        addBtn.addActionListener(e -> addLesson());
        editBtn.addActionListener(e -> editLesson());
        delBtn.addActionListener(e -> deleteLesson());

        // refresh when focused
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                refresh();
            }
        });
    }

    private void refresh() {
        lessons = courseService.getLessons(course);
        lessonList.setListData(lessons.stream().map(Lesson::getTitle).toArray(String[]::new));
    }

    private void addLesson() {
        JTextField title = new JTextField();
        JTextArea content = new JTextArea(8, 40);
        JPanel p = new JPanel(new BorderLayout(6,6));
        p.add(new JLabel("Title:"), BorderLayout.NORTH);
        p.add(title, BorderLayout.CENTER);
        p.add(new JLabel("Content:"), BorderLayout.SOUTH);

        int res1 = JOptionPane.showConfirmDialog(this, new Object[]{ "Title:", title, "Content:", new JScrollPane(content) }, "Add Lesson", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res1 != JOptionPane.OK_OPTION) return;
        String t = title.getText().trim();
        String c = content.getText().trim();
        if (t.isEmpty() || c.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both title and content required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = courseService.addLesson(course, t, c);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Add lesson failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Lesson added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refresh();
    }

    private void editLesson() {
        int idx = lessonList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select lesson to edit.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Lesson lesson = lessons.get(idx);
        JTextField title = new JTextField(lesson.getTitle());
        JTextArea content = new JTextArea(lesson.getContent(), 8, 40);
        int res = JOptionPane.showConfirmDialog(this, new Object[]{ "Title:", title, "Content:", new JScrollPane(content) }, "Edit Lesson", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        String t = title.getText().trim();
        String c = content.getText().trim();
        if (t.isEmpty() || c.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both title and content required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = courseService.updateLesson(lesson, t, c);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Lesson updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refresh();
    }

    private void deleteLesson() {
        int idx = lessonList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select lesson to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Lesson lesson = lessons.get(idx);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete lesson '" + lesson.getTitle() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = courseService.deleteLesson(course, lesson);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Lesson deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refresh();
    }
}