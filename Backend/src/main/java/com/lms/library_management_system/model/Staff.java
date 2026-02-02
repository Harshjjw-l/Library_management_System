package com.lms.library_management_system.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    private String sIdNo;

    private String firstName;
    private String lastName;
    private String position;
    private String phone;
    private String emailId;

    @ManyToMany
    @JoinTable(
        name = "staff_tracks_student",
        joinColumns = @JoinColumn(name = "s_id_no"),
        inverseJoinColumns = @JoinColumn(name = "prn_no")
    )
    private Set<Student> trackedStudents;

    // Getters and Setters...
}