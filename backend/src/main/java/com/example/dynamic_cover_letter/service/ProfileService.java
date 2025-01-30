package com.example.dynamic_cover_letter.service;

import com.example.dynamic_cover_letter.entity.Profile;
import com.example.dynamic_cover_letter.entity.User;
import com.example.dynamic_cover_letter.repository.ProfileRepository;
import com.example.dynamic_cover_letter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    // Find a profile by user email
    public Profile findProfileByEmail(String email) {
        logger.info("Fetching profile for email: {}", email);
        return profileRepository.findByUserEmail(email);
    }

    // Create or update a profile
    public Profile createOrUpdateProfile(Profile profile) {
        logger.info("Creating or updating profile for user: {}", 
                    profile.getUser() != null ? profile.getUser().getEmail() : "null");

        if (profile.getUser() == null || profile.getUser().getEmail() == null) {
            throw new IllegalArgumentException("User information is required to create or update a profile.");
        }

        // Fetch the User entity from the database
        User existingUser = userRepository.findByEmail(profile.getUser().getEmail());
        if (existingUser == null) {
            throw new IllegalArgumentException("User does not exist. Profile cannot be created.");
        }

        // Set the fetched User in the Profile
        profile.setUser(existingUser);

        // Check if the profile already exists
        Profile existingProfile = profileRepository.findByUserEmail(existingUser.getEmail());
        if (existingProfile != null) {
            logger.info("Profile found, updating profile for user: {}", profile.getUser().getEmail());
            existingProfile.setFirstName(profile.getFirstName());
            existingProfile.setLastName(profile.getLastName());
            existingProfile.setEducation(profile.getEducation());
            existingProfile.setWorkExperience(profile.getWorkExperience());
            existingProfile.setSkills(profile.getSkills());
            existingProfile.setCertifications(profile.getCertifications());
            return profileRepository.save(existingProfile);
        } else {
            logger.info("No profile found, creating new profile for user: {}", profile.getUser().getEmail());
            return profileRepository.save(profile);
        }
    }
}
