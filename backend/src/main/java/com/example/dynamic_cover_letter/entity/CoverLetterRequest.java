package com.example.dynamic_cover_letter.entity;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoverLetterRequest {
    private List<String> certifications;
    private List<Education> education;
    private String selectedTemplate;
    private List<String> skills;
    @JsonProperty("userInfo")
    private User user;
    @JsonProperty("workExperience")
    private List<Experience> experience;

    
    // Getters and Setters
    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public String getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(String selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Experience> getExperience() {
        return experience;
    }

    public void setExperience(List<Experience> experience) {
        this.experience = experience;
    }
}