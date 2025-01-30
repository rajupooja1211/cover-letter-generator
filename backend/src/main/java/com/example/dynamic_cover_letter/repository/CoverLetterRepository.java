package com.example.dynamic_cover_letter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.example.dynamic_cover_letter.entity.*;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {
    List<CoverLetter> findByUserId(Long userId);
    List<CoverLetter> findByEmail(String email);
}
