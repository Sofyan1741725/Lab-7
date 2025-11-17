package com.example.services;

import com.example.models.*;
import com.example.database.jsonDataBaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.util.function.Function;

public class CourseService {

    private static CourseService instance;
    private final jsonDataBaseManager<Course> courseDB;
    private final jsonDataBaseManager<User> userDB;
    private final LessonService lessonService;

    private CourseService() {
        courseDB = new jsonDataBaseManager<>("courses.json", Course.class, "courseId");

        // ===================== User custom deserializer =====================
        Function<JsonObject, User> userDeserializer = json -> {
            String role = json.has("role") ? json.get("role").getAsString() : "Student";
            Gson gson = new Gson();
            if ("Instructor".equalsIgnoreCase(role)) {
                return gson.fromJson(json, Instructor.class);
            } else {
                return gson.fromJson(json, Student.class);
            }
        };

        userDB = new jsonDataBaseManager<>("users.json", User.class, "userId", null, userDeserializer);
        lessonService = LessonService.getInstance();
    }

    public static CourseService getInstance() {
        if (instance == null) instance = new CourseService();
        return instance;
    }

    // ================= Instructor Methods =================
    public boolean createCourse(Instructor instructor, String title, String description) {
        if (instructor == null || title == null || title.isEmpty()) return false;
        if (instructor.getCreatedCourses().stream().anyMatch(c -> c.getTitle().equals(title))) return false;

        Course course = new Course(title, description, instructor);
        instructor.getCreatedCourses().add(course);
        courseDB.add(course);
        userDB.updateById(String.valueOf(instructor.getUserId()), instructor);
        return true;
    }

    public boolean updateCourse(Course course, String newTitle, String newDesc) {
        if (course == null) return false;
        if (newTitle != null && !newTitle.isEmpty()) course.setTitle(newTitle);
        if (newDesc != null && !newDesc.isEmpty()) course.setDescription(newDesc);
        courseDB.updateById(String.valueOf(course.getCourseId()), course);
        return true;
    }

    public boolean deleteCourse(Course course) {
        if (course == null) return false;
        courseDB.deleteById(String.valueOf(course.getCourseId()));
        Instructor ins = course.getInstructor();
        ins.getCreatedCourses().remove(course);
        userDB.updateById(String.valueOf(ins.getUserId()), ins);
        return true;
    }

    public List<Course> getCoursesByInstructor(Instructor instructor) {
        if (instructor == null) return new ArrayList<>();
        // بدل equals، نعمل مقارنة على userId
        return courseDB.getAll().stream()
                .filter(c -> c.getInstructor() != null && c.getInstructor().getUserId() == instructor.getUserId())
                .collect(Collectors.toList());
    }

    // ================= Student Methods =================
    public boolean enrollStudent(Student student, Course course) {
        if (student == null || course == null) return false;
        if (student.getEnrolledCourses().contains(course)) return false;
        student.getEnrolledCourses().add(course);
        userDB.updateById(String.valueOf(student.getUserId()), student);
        return true;
    }

    public List<Course> getEnrolledCourses(Student student) {
        return student.getEnrolledCourses();
    }

    public List<Course> getAllCourses() {
        return courseDB.getAll();
    }

    public List<String> getEnrolledStudentNames(Course course) {
        return userDB.getAll().stream()
                .filter(u -> u instanceof Student && ((Student) u).getEnrolledCourses().contains(course))
                .map(User::getUserName)
                .collect(Collectors.toList());
    }

    // ================= Lessons =================
    public List<Lesson> getLessons(Course course) {
        return lessonService.getLessonsByCourse(course);
    }

    public boolean addLesson(Course course, String title, String content) {
        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setContent(content);
        return lessonService.addLessonToCourse(course, lesson);
    }

    public boolean updateLesson(Lesson lesson, String newTitle, String newContent) {
        return lessonService.editLesson(lesson, newTitle, newContent, null);
    }

    public boolean deleteLesson(Course course, Lesson lesson) {
        course.getLessons().remove(lesson);
        return lessonService.deleteLesson(course, lesson);
    }

    public boolean markLessonCompleted(Student student, Course course, Lesson lesson) {
        return lessonService.completeLesson(student, lesson);
    }

    public String getProgressSummary(Student student, Course course) {
        int total = course.getLessons().size();
        int completed = student.getCompletedLessons(course).size(); // افترض getCompletedLessons موجودة في Student
        return total == 0 ? "No lessons yet." : (completed * 100 / total) + "% completed (" + completed + "/" + total + ")";
    }
}
