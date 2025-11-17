package com.example.models;
import java.util.ArrayList;

public class Instructor extends User
{
    private ArrayList<Course> createdCourses;

    public Instructor(int userId, String userName, String email, String password) {
        super(userId, userName, email, password);
        this.createdCourses = new ArrayList<>();
    }

    public Instructor() {
        this.createdCourses = new ArrayList<>();
    }


    public void setCreatedCourses(ArrayList<Course> createdCourses) {this.createdCourses = createdCourses;}
    public ArrayList<Course> getCreatedCourses() {return new ArrayList<>(createdCourses);
    }

    public void createCourse(String title, String description)
    {
        for(Course c : createdCourses)
        {
            if (c.getTitle() != null && c.getTitle().equals(title))
            {
                System.out.println("This Course Is Already Exists..");
                return;
            }
        }
        Course course = new Course(title, description,this);
        createdCourses.add(course);
    }

    public void uploadLesson(Course course, Lesson lesson)
    {
        if (createdCourses.contains(course)) {
            course.addLesson(lesson);
        } else {
            System.out.println("You Can't Add a Lesson To a Course You Didn't Create..");
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        return this.getUserId() == that.getUserId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getUserId());
    }



}
