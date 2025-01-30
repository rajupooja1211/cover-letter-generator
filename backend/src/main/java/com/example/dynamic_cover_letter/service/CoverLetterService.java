package com.example.dynamic_cover_letter.service;

import com.example.dynamic_cover_letter.entity.CoverLetter;
import com.example.dynamic_cover_letter.repository.CoverLetterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoverLetterService {

    @Autowired
    private CoverLetterRepository coverLetterRepository;

    public CoverLetter createCoverLetter(CoverLetter coverLetter) {
        return coverLetterRepository.save(coverLetter);
    }

    public void deleteCoverLetter(Long id) {
        coverLetterRepository.deleteById(id);
    }
}
