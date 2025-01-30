package com.example.dynamic_cover_letter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping(value = {"/", "/login", "/register"})
    public String serveFrontend() {
        return "index.html";
    }
}
