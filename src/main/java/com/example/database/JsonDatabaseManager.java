package com.example.database;

import com.example.models.*;
import com.example.utils.RuntimeTypeAdapterFactory;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonDatabaseManager {

    private static JsonDatabaseManager instance;

    private final String usersFilePath = "src/main/resources/users.json";
    private final String coursesFilePath = "src/main/resources/courses.json";

    private List<User> users;
    private List<Course> courses;

    private final Gson gson;

    private JsonDatabaseManager() {

        // Register polymorphism for User → Student / Instructor
        RuntimeTypeAdapterFactory<User> userAdapterFactory = RuntimeTypeAdapterFactory
                .of(User.class, "role")
                .registerSubtype(Student.class, "student")
                .registerSubtype(Instructor.class, "instructor");

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(userAdapterFactory)
                .setPrettyPrinting()
                .create();

        ensureFilesExist();

        users = loadUsers();
        courses = loadCourses();

        // Fix references inside objects (instructor, lessons, students…)
        fixCourseRelationships();

        // Fix counters for Course + Lesson
        fixCounters();
    }

    public static JsonDatabaseManager getInstance() {
        if (instance == null) instance = new JsonDatabaseManager();
        return instance;
    }

    // =====================================================
    //                 FILE INITIALIZATION
    // =====================================================

    private void ensureFilesExist() {
        try {
            File dir = new File("src/main/resources");
            if (!dir.exists()) dir.mkdirs();

            File usersFile = new File(usersFilePath);
            File coursesFile = new File(coursesFilePath);

            if (!usersFile.exists() || usersFile.length() == 0)
                try (FileWriter fw = new FileWriter(usersFile)) { fw.write("[]"); }

            if (!coursesFile.exists() || coursesFile.length() == 0)
                try (FileWriter fw = new FileWriter(coursesFile)) { fw.write("[]"); }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    //                    LOADING DATA
    // =====================================================

    private List<User> loadUsers() {
        try (Reader reader = new FileReader(usersFilePath)) {
            List<User> list = gson.fromJson(reader, new TypeToken<List<User>>(){}.getType());
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error loading users.json → empty list");
            return new ArrayList<>();
        }
    }

    private List<Course> loadCourses() {
        try (Reader reader = new FileReader(coursesFilePath)) {
            List<Course> list = gson.fromJson(reader, new TypeToken<List<Course>>(){}.getType());
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error loading courses.json → empty list");
            return new ArrayList<>();
        }
    }

    // =====================================================
    //                 FIX RELATIONSHIPS
    // =====================================================

    private void fixCourseRelationships() {

        for (Course c : courses) {

            // =========== FIX INSTRUCTOR ===========
            if (c.getInstructorEmail() != null) {
                Instructor inst = (Instructor) users.stream()
                        .filter(u -> u instanceof Instructor && u.getEmail().equals(c.getInstructorEmail()))
                        .findFirst().orElse(null);

                c.setInstructor(inst);

                if (inst != null) {
                    if (inst.getCreatedCourses() == null)
                        inst.setCreatedCourses(new ArrayList<>());

                    if (!inst.getCreatedCourses().contains(c))
                        inst.getCreatedCourses().add(c);
                }
            }

            // ============ FIX LESSONS ============
            if (c.getLessons() == null)
                c.setLessons(new ArrayList<>());

            // ============ FIX STUDENTS ============
            if (c.getEnrolledStudents() == null)
                c.setEnrolledStudents(new ArrayList<>());
        }

        // ============ FIX STUDENT COURSE LIST ============

        for (User u : users) {
            if (u instanceof Student s) {

                if (s.getEnrolledCourseIds() == null)
                    continue;

                List<Course> enrolledList = new ArrayList<>();

                for (Integer id : s.getEnrolledCourseIds()) {
                    Course found = courses.stream()
                            .filter(cr -> cr.getCourseId() == id)
                            .findFirst().orElse(null);

                    if (found != null)
                        enrolledList.add(found);
                }
            }
        }
    }

    // =====================================================
    //                   FIX COUNTERS
    // =====================================================

    private void fixCounters() {

        int maxCourseId = 0;
        int maxLessonId = 0;

        for (Course c : courses) {

            if (c.getCourseId() > maxCourseId)
                maxCourseId = c.getCourseId();

            if (c.getLessons() != null) {
                for (Lesson l : c.getLessons()) {
                    if (l.getLessonId() > maxLessonId)
                        maxLessonId = l.getLessonId();
                }
            }
        }

        Course.setCourseCounter(maxCourseId + 1);
        Lesson.setLessonCounter(maxLessonId + 1);
    }

    // =====================================================
    //                       SAVE & ADD
    // =====================================================

    public void saveUsers() {
        try (Writer writer = new FileWriter(usersFilePath)) {
            gson.toJson(users, writer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveCourses() {
        try (Writer writer = new FileWriter(coursesFilePath)) {
            gson.toJson(courses, writer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void addUser(User user) {
        if (user == null) return;

        users.add(user);
        saveUsers();
    }

    public void addCourse(Course course) {
        if (course == null) return;

        courses.add(course);
        saveCourses();
    }

    // =====================================================
    //                  GETTERS
    // =====================================================

    public List<User> getUsers() { return users; }
    public List<Course> getCourses() { return courses; }
}
