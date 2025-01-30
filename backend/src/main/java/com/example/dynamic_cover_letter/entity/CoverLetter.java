package com.example.dynamic_cover_letter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cover_letter")
public class CoverLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String template;
    private String companyPosition;
    private String email;
    @ManyToOne
    @JoinColumn(name = "user_id") // This maps the user_id column in the cover_letter table
    private User user;
    @Lob
    @Column(name = "file_data", columnDefinition = "MEDIUMBLOB") // Ensure MEDIUMBLOB for large data
    private byte[] fileData;

    //getters and setters
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCompanyPosition() {
        return companyPosition;
    }

    public void setCompanyPosition(String companyPosition) {
        this.companyPosition = companyPosition;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
