package models;

import java.util.ArrayList;

public class Course {

    private int courseId;
    private String title;
    private String description;
    private Instructor instructor;
    private ArrayList<Lesson> lessons;

    public Course() {
        this.lessons = new ArrayList<>();
    }

    public Course(String title, String description, Instructor instructor) {
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.lessons = new ArrayList<>();
    }

    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Instructor getInstructor() { return instructor; }
    public ArrayList<Lesson> getLessons() { return lessons; }

    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public void setLessons(ArrayList<Lesson> lessons) { this.lessons = lessons; }

    public void addLesson(Lesson lesson) {
        if (!lessons.contains(lesson)) {
            lessons.add(lesson);
            System.out.println("Lesson Added Successfully.");
        } else {
            System.out.println("This Lesson Already Exists.");
        }
    }
}
