package com.example.dynamic_cover_letter.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> education;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> workExperience;
    @ElementCollection
    private List<String> skills;
    @ElementCollection
    private List<String> certifications;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public List<Experience> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(List<Experience> workExperience) {
        this.workExperience = workExperience;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
