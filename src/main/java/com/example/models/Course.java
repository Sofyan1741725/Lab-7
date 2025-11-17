package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private static int nextId = 1;

    private int courseId;
    private String title;
    private String description;
    private Instructor instructor;
    private ArrayList<Lesson> lessons;

    public Course() {
        this.courseId = nextId++;
        this.lessons = new ArrayList<>();
    }

    public Course(String title, String description, Instructor instructor) {
        this.courseId = nextId++;
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.lessons = new ArrayList<>();
    }

    // ========= Getters & Setters =========
    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Instructor getInstructor() { return instructor; }
    public List<Lesson> getLessons() { return lessons; }

    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public void setLessons(ArrayList<Lesson> lessons) { this.lessons = lessons; }

    // إضافة درس جديد
    public void addLesson(Lesson lesson) {
        if (!lessons.contains(lesson)) {
            lessons.add(lesson);
            lesson.setCourse(this); // link lesson to this course
            System.out.println("Lesson added successfully: " + lesson.getTitle());
        } else {
            System.out.println("Lesson already exists: " + lesson.getTitle());
        }
    }
}
