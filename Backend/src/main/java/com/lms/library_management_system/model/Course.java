package com.lms.library_management_system.model;

import jakarta.persistence.*;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
        name = "course_syllabus_topic",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> syllabusTopics;

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Set<Topic> getSyllabusTopics() { return syllabusTopics; }
    public void setSyllabusTopics(Set<Topic> syllabusTopics) { this.syllabusTopics = syllabusTopics; }
}