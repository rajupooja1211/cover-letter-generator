package com.example.dynamic_cover_letter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Column;


@Entity
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    @Column(name = "years_worked")
    private String yearsWorked;
    @Lob // Use this annotation to mark the field as a large object
    @Column(columnDefinition = "TEXT") // Optional: Ensures it's stored as TEXT
    private String description;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getYearsWorked() {
        return yearsWorked;
    }

    public void setYearsWorked(String yearsWorked) {
        this.yearsWorked = yearsWorked;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
