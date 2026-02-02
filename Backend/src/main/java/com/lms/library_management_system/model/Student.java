package com.lms.library_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(length = 20)
    private String prnNo;

    @Column(length = 50)
    private String firstName;

    // ADD THIS LINE
    @Column(length = 50)
    private String middleName;

    @Column(length = 50)
    private String lastName;

    // ADD THIS LINE
    private LocalDate dob;

    @Column(length = 10)
    private String gender;
    
    private String photo;

    @ManyToOne
    @JoinColumn(name = "room_no")
    private StudentClass studentClass;

    @ManyToMany
    @JoinTable(
        name = "student_takes_course",
        joinColumns = @JoinColumn(name = "prn_no"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses;


    // --- Getters and Setters ---

    public String getPrnNo() {
        return prnNo;
    }

    public void setPrnNo(String prnNo) {
        this.prnNo = prnNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public StudentClass getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(StudentClass studentClass) {
        this.studentClass = studentClass;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}