package com.lms.library_management_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    private String courseId;

    private String name;
    private Integer credits;

    @ManyToOne
    @JoinColumn(name = "dept_no")
    private Department department;

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}