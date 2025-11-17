package com.example.models;

public class Lesson {

    private static int nextId = 1;

    private int lessonId;
    private String title;
    private String content;
    private String resourceLink;
    private Course course;

    public Lesson(String title, String content, String resourceLink, Course course) {
        this.lessonId = nextId++;
        this.title = title;
        this.content = content;
        this.resourceLink = resourceLink;
        this.course = course;
    }

    public Lesson() {
        this.lessonId = nextId++;
    }

    // ========= Getters & Setters =========
    public int getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getResourceLink() { return resourceLink; }
    public Course getCourse() { return course; }

    public void setLessonId(int lessonId) { this.lessonId = lessonId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setResourceLink(String resourceLink) { this.resourceLink = resourceLink; }
    public void setCourse(Course course) { this.course = course; }

    // عرض محتوى الدرس
    public void displayContent() {
        System.out.println("Lesson: " + title);
        System.out.println(content);
        if (resourceLink != null && !resourceLink.isEmpty())
            System.out.println("Resource: " + resourceLink);
    }
}
