package com.lms.library_management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    private String deptNo;

    private String name;
    private String hod;
    private String phone;
    private String emailId;

    // Getters and Setters
    public String getDeptNo() { return deptNo; }
    public void setDeptNo(String deptNo) { this.deptNo = deptNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHod() { return hod; }
    public void setHod(String hod) { this.hod = hod; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
}