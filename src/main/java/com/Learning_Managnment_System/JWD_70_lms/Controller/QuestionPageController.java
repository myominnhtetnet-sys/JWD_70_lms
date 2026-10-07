package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionPageController {

    @GetMapping("/questions")
    public String questionList() {
        return "question-list";
    }

    @GetMapping("/questions/new")
    public String newQuestion() {
        return "question-form";
    }

    @GetMapping("/questions/edit/{id}")
    public String editQuestion(@PathVariable("id") Long id) {
        return "question-form";
    }
}