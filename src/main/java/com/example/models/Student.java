package com.example.models;
import java.util.ArrayList;

public class Student extends User
{
    private ArrayList<Course> enrolledCourses ;

    public Student(int userId, String userName, String email, String password)
 {
     super(userId, userName, email, password);
     this.enrolledCourses = new ArrayList<>();
 }

    public Student() {
        this.enrolledCourses = new ArrayList<>();
    }

    public void enrollInCourse(Course course)
    {
        if(!enrolledCourses.contains(course))
        {
            enrolledCourses.add(course);
            System.out.println("Enrolled Successfully..");
        }
        else System.out.println("You Already Enrolled In This Course..");
    };
    public void completeLesson(Lesson lesson) {
        System.out.println(getUserName() + " completed lesson: " + lesson.getTitle());
    }







    public ArrayList<Course> getEnrolledCourses() {return new ArrayList<>(enrolledCourses);}

}
