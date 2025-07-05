package com.example.peopoolbe.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/swagger-ui.html";
    }
}
