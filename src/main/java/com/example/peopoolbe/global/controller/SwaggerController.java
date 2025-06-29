package com.example.peopoolbe.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerController {
    @RequestMapping("/")
    public String homeRedirect() {
        return "redirect:/swagger-ui.html";
    }
}
