package com.example.dynamic_cover_letter.repository;

import com.example.dynamic_cover_letter.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("SELECT p FROM Profile p WHERE p.user.email = :email")
    Profile findByUserEmail(@Param("email") String email);
}
