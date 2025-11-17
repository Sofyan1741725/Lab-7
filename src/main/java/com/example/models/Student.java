package com.example.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {

    private List<Course> enrolledCourses;
    private Map<Integer, List<Lesson>> completedLessons; // key: courseId

    public Student(int userId, String userName, String email, String password) {
        super(userId, userName, email, password);
        this.enrolledCourses = new ArrayList<>();
        this.completedLessons = new HashMap<>();
    }

    public List<Course> getEnrolledCourses() {
        if (enrolledCourses == null) enrolledCourses = new ArrayList<>();
        return enrolledCourses;
    }

    public void addCompletedLesson(Lesson lesson) {
        if (lesson == null) return;
        int courseId = lesson.getCourse().getCourseId();
        completedLessons.putIfAbsent(courseId, new ArrayList<>());
        if (!completedLessons.get(courseId).contains(lesson)) {
            completedLessons.get(courseId).add(lesson);
        }
    }

    public List<Lesson> getCompletedLessons(Course course) {
        if (course == null) return new ArrayList<>();
        return completedLessons.getOrDefault(course.getCourseId(), new ArrayList<>());
    }

    // **الـ method الجديدة اللي ترجّع عدد الدروس المكتملة مباشرة**
    public int getCompletedLessonCount(Course course) {
        return getCompletedLessons(course).size();
    }
}
