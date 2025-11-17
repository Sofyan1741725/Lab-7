package com.example.services;

import com.example.models.Course;
import com.example.models.Student;
import com.example.models.Instructor;
import com.example.models.User;
import com.example.database.jsonDataBaseManager;

import java.util.List;
import java.util.Optional;

public class CourseService {

    
    
    private final LessonService lessonService = new LessonService("lessons.json", "courses.json", "users.json");
    private final jsonDataBaseManager<Course> courseDB;
    private final jsonDataBaseManager<User> userDB;

    public CourseService(String courseJsonPath, String userJsonPath) {
        this.courseDB = new jsonDataBaseManager<>(courseJsonPath, Course.class, "courseId");
        this.userDB = new jsonDataBaseManager<>(userJsonPath, User.class, "userId");
    }

    // ================= Instructor Methods =================
    public boolean createCourse(Instructor instructor, String title, String description) {
        if (instructor == null || title == null || title.isEmpty()) {
            System.out.println("Instructor and course title are required.");
            return false;
        }

        // Check for duplicate title in instructor's created courses
        if (instructor.getCreatedCourses().stream()
                .anyMatch(c -> title.equals(c.getTitle()))) {
            System.out.println("Course with this title already exists.");
            return false;
        }

        Course course = new Course(title, description, instructor);
        instructor.getCreatedCourses().add(course);
        courseDB.add(course);

        // Update instructor in userDB
        userDB.updateById(String.valueOf(instructor.getUserId()), instructor);

        System.out.println("Course created successfully: " + title);
        return true;
    }

    public boolean editCourse(Instructor instructor, String courseId, String newTitle, String newDescription) {
        if (instructor == null || courseId == null) return false;

        Optional<Course> optionalCourse = courseDB.getById(courseId);
        if (optionalCourse.isEmpty()) {
            System.out.println("Course not found.");
            return false;
        }

        Course course = optionalCourse.get();

        if (!instructor.getCreatedCourses().contains(course)) {
            System.out.println("Instructor cannot edit a course they didn't create.");
            return false;
        }

        if (newTitle != null && !newTitle.isEmpty()) course.setTitle(newTitle);
        if (newDescription != null) course.setDescription(newDescription);

        courseDB.updateById(courseId, course);
        System.out.println("Course updated successfully.");
        return true;
    }

    // ================= Student Methods =================
    public boolean enrollCourse(Student student, String courseId) {
        if (student == null || courseId == null) return false;

        Optional<Course> optionalCourse = courseDB.getById(courseId);
        if (optionalCourse.isEmpty()) {
            System.out.println("Course not found.");
            return false;
        }

        Course course = optionalCourse.get();

        if (student.getEnrolledCourses().contains(course)) {
            System.out.println("Student already enrolled in this course.");
            return false;
        }

        // Only update student's enrolled courses
        student.getEnrolledCourses().add(course);
        userDB.updateById(String.valueOf(student.getUserId()), student);

        System.out.println(student.getUserName() + " enrolled in " + course.getTitle());
        return true;
    }

    // ================= Browsing =================
    public List<Course> getAllCourses() {
        return courseDB.getAll();
    }

    public Optional<Course> getCourseById(String courseId) {
        return courseDB.getById(courseId);
    }

    public void printAllCourses() {
        List<Course> courses = courseDB.getAll();
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
        } else {
            System.out.println("Available Courses:");
            for (Course c : courses) {
                System.out.println("- " + c.getTitle() + " (Instructor: " + c.getInstructor().getUserName() + ")");
            }
        }
    }
}



public class CourseService {
    
}
