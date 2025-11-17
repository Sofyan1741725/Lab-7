package com.example.services;

import com.example.models.Lesson;
import com.example.models.Course;
import com.example.models.Student;
import com.example.database.jsonDataBaseManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class LessonService {

    private static LessonService instance;
    private final jsonDataBaseManager<Lesson> lessonDB;
    private final jsonDataBaseManager<Course> courseDB;
    private final jsonDataBaseManager<Student> studentDB;

    private LessonService() {
        lessonDB = new jsonDataBaseManager<>("lessons.json", Lesson.class, "lessonId");
        courseDB = new jsonDataBaseManager<>("courses.json", Course.class, "courseId");
        studentDB = new jsonDataBaseManager<>("users.json", Student.class, "userId");
    }

    public static LessonService getInstance() {
        if (instance == null) instance = new LessonService();
        return instance;
    }

    public boolean addLessonToCourse(Course course, Lesson lesson) {
        if (course == null || lesson == null) return false;
        lesson.setCourse(course);
        course.addLesson(lesson);
        lessonDB.add(lesson);
        courseDB.updateById(String.valueOf(course.getCourseId()), course);
        return true;
    }

    public boolean editLesson(Lesson lesson, String newTitle, String newContent, String newResourceLink) {
        if (lesson == null) return false;
        if (newTitle != null) lesson.setTitle(newTitle);
        if (newContent != null) lesson.setContent(newContent);
        if (newResourceLink != null) lesson.setResourceLink(newResourceLink);
        lessonDB.updateById(String.valueOf(lesson.getLessonId()), lesson);
        return true;
    }

    public List<Lesson> getLessonsByCourse(Course course) {
        if (course == null) return new ArrayList<>();
        return course.getLessons();
    }

    public boolean completeLesson(Student student, Lesson lesson) {
        if (student == null || lesson == null) return false;
        student.addCompletedLesson(lesson); // لازم تضيف method في Student class
        studentDB.updateById(String.valueOf(student.getUserId()), student);
        return true;
    }

    public boolean deleteLesson(Course course, Lesson lesson) {
        if (course == null || lesson == null) return false;
        lessonDB.deleteById(String.valueOf(lesson.getLessonId()));
        course.getLessons().remove(lesson);
        courseDB.updateById(String.valueOf(course.getCourseId()), course);
        return true;
    }

    public Optional<Lesson> getLessonById(String lessonId) {
        return lessonDB.getById(lessonId);
    }

    public void displayLesson(Lesson lesson) {
        if (lesson == null) System.out.println("Lesson not found.");
        else lesson.displayContent();
    }
}
