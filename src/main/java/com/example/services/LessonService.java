package com.example.services;

import com.example.models.Lesson;
import com.example.models.Course;
import com.example.models.Student;
import com.example.database.jsonDataBaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LessonService {

    private final jsonDataBaseManager<Lesson> lessonDB;
    private final jsonDataBaseManager<Course> courseDB;
    private final jsonDataBaseManager<Student> studentDB;

    public LessonService(String lessonJsonPath, String courseJsonPath, String studentJsonPath) {
        this.lessonDB = new jsonDataBaseManager<>(lessonJsonPath, Lesson.class, "lessonId");
        this.courseDB = new jsonDataBaseManager<>(courseJsonPath, Course.class, "courseId");
        this.studentDB = new jsonDataBaseManager<>(studentJsonPath, Student.class, "userId");
    }

    // ================= Instructor Methods =================
    public boolean addLessonToCourse(Course course, Lesson lesson) {
        if (course == null || lesson == null) return false;

        // Link lesson to course
        lesson.setCourse(course);
        course.addLesson(lesson);

        // Persist
        lessonDB.add(lesson);
        courseDB.updateById(String.valueOf(course.getCourseId()), course);

        System.out.println("Lesson added: " + lesson.getTitle() + " to course: " + course.getTitle());
        return true;
    }

    public boolean editLesson(Lesson lesson, String newTitle, String newContent, String newResourceLink) {
        if (lesson == null) return false;

        if (newTitle != null && !newTitle.isEmpty()) lesson.setTitle(newTitle);
        if (newContent != null) lesson.setContent(newContent);
        if (newResourceLink != null) lesson.setResourceLink(newResourceLink);

        lessonDB.updateById(String.valueOf(lesson.getLessonId()), lesson);
        System.out.println("Lesson updated: " + lesson.getTitle());
        return true;
    }

    // ================= Student Methods =================
    public List<Lesson> getLessonsByCourse(Course course) {
        if (course == null) return new ArrayList<>();
        return course.getLessons();
    }

    public boolean completeLesson(Student student, Lesson lesson) {
        if (student == null || lesson == null) return false;

        // For now, we store completion as a simple log (can be expanded later)
        System.out.println(student.getUserName() + " completed lesson: " + lesson.getTitle());

        // Optional: persist student progress
        // If you want to track progress in student object, you could add:
        // student.getCompletedLessons().add(lesson);
        studentDB.updateById(String.valueOf(student.getUserId()), student);

        return true;
    }

    // ================= Utility Methods =================
    public Optional<Lesson> getLessonById(String lessonId) {
        return lessonDB.getById(lessonId);
    }

    public void displayLesson(Lesson lesson) {
        if (lesson == null) {
            System.out.println("Lesson not found.");
            return;
        }
        lesson.displayContent();
    }
public class LessonService {
    
}
