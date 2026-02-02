package com.lms.library_management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "publishers")
public class Publisher {

    @Id
    private String pubId;

    private String name;
    
    // Getters and Setters
    public String getPubId() { return pubId; }
    public void setPubId(String pubId) { this.pubId = pubId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}