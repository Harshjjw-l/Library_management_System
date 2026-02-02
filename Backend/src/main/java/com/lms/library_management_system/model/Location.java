package com.lms.library_management_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String shelfNo;
    private String rowNo;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getShelfNo() { return shelfNo; }
    public void setShelfNo(String shelfNo) { this.shelfNo = shelfNo; }
    public String getRowNo() { return rowNo; }
    public void setRowNo(String rowNo) { this.rowNo = rowNo; }
}