package com.lms.library_management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classes")
public class StudentClass {

    @Id
    private String roomNo;

    private String division;
    private String branch;
    private String gfm;

    // Getters and Setters
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getGfm() { return gfm; }
    public void setGfm(String gfm) { this.gfm = gfm; }
}