package com.example.dynamic_cover_letter.controller;

import com.example.dynamic_cover_letter.entity.Profile;
import com.example.dynamic_cover_letter.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<Profile> getProfile(@RequestParam String email) {
        Profile profile = profileService.findProfileByEmail(email);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Return 204 No Content if profile doesn't exist
        }
        return ResponseEntity.ok(profile); // Return 200 OK with the profile
    }

    @PostMapping
    public ResponseEntity<Profile> createOrUpdateProfile(@RequestBody Profile profile) {
        System.out.println("Received Profile: " + profile);
        Profile savedProfile = profileService.createOrUpdateProfile(profile);
        return ResponseEntity.ok(savedProfile);
    }
}
