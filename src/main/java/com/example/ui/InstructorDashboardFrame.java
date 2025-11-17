package com.example.ui;

import com.example.models.Course;
import com.example.models.Instructor;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InstructorDashboardFrame extends JFrame {

    private final Instructor instructor;
    private final CourseService courseService;
    private JList<String> courseList;
    private List<Course> myCourses;

    public InstructorDashboardFrame(Instructor instructor) {
        this.instructor = instructor;
        this.courseService = CourseService.getInstance();

        setTitle("Instructor Dashboard - " + instructor.getUserName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(root);

        // Top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Logged in as: " + instructor.getUserName()));
        JButton logoutBtn = new JButton("Logout");
        top.add(logoutBtn);
        root.add(top, BorderLayout.NORTH);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // Center panel: course list + controls
        JPanel center = new JPanel(new BorderLayout(8,8));
        myCourses = courseService.getCoursesByInstructor(instructor);
        courseList = new JList<>(myCourses.stream().map(Course::getTitle).toArray(String[]::new));
        center.add(new JScrollPane(courseList), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        JButton createBtn = new JButton("Create Course");
        JButton editBtn = new JButton("Edit Course");
        JButton deleteBtn = new JButton("Delete Course");
        JButton manageLessonsBtn = new JButton("Manage Lessons");
        JButton viewStudentsBtn = new JButton("View Enrolled Students");
        controls.add(createBtn);
        controls.add(Box.createVerticalStrut(8));
        controls.add(editBtn);
        controls.add(Box.createVerticalStrut(8));
        controls.add(deleteBtn);
        controls.add(Box.createVerticalStrut(8));
        controls.add(manageLessonsBtn);
        controls.add(Box.createVerticalStrut(8));
        controls.add(viewStudentsBtn);

        center.add(controls, BorderLayout.EAST);
        root.add(center, BorderLayout.CENTER);

        // Actions
        createBtn.addActionListener(e -> new CreateCourseFrame(instructor).setVisible(true));
        editBtn.addActionListener(e -> doEditCourse());
        deleteBtn.addActionListener(e -> doDeleteCourse());
        manageLessonsBtn.addActionListener(e -> doManageLessons());
        viewStudentsBtn.addActionListener(e -> doViewStudents());

        // Refresh list when window gains focus
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                refreshList();
            }
        });

        // Initial refresh to ensure courses show up
        refreshList();
    }

    private void refreshList() {
        myCourses = courseService.getCoursesByInstructor(instructor);
        // Debug: تأكد من الكورسات
        System.out.println("Courses for instructor " + instructor.getUserName() + ":");
        myCourses.forEach(c -> System.out.println(c.getTitle() + " - ID: " + c.getCourseId()));
        courseList.setListData(myCourses.stream().map(Course::getTitle).toArray(String[]::new));
    }

    private void doEditCourse() {
        int idx = courseList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to edit.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = myCourses.get(idx);
        new EditCourseFrame(c).setVisible(true);
    }

    private void doDeleteCourse() {
        int idx = courseList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = myCourses.get(idx);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete course '" + c.getTitle() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = courseService.deleteCourse(c);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Course deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshList();
    }

    private void doManageLessons() {
        int idx = courseList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to manage lessons.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = myCourses.get(idx);
        new LessonManagementFrame(c).setVisible(true);
    }

    private void doViewStudents() {
        int idx = courseList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = myCourses.get(idx);
        java.util.List<String> students = courseService.getEnrolledStudentNames(c);
        if (students == null || students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No enrolled students yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JList<String> lst = new JList<>(students.toArray(new String[0]));
        JOptionPane.showMessageDialog(this, new JScrollPane(lst), "Enrolled Students", JOptionPane.PLAIN_MESSAGE);
    }
}
