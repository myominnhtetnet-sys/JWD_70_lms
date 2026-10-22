package com.Learning_Managnment_System.JWD_70_lms.Controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

@Controller
public class QuestionPageController {

    private void addCommonAttributes(HttpSession session, Model model) {
        LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.getFull_name() != null) {
            model.addAttribute("fullName", currentUser.getFull_name());
        } else if (session.getAttribute("fullName") != null) {
            model.addAttribute("fullName", session.getAttribute("fullName"));
        } else {
            model.addAttribute("fullName", "Teacher Account");
        }
    }

    // Handles both /questions, /question-list, /teacher/questions, and /teacher/question-list
    @GetMapping({"/questions", "/question-list", "/teacher/questions", "/teacher/question-list"})
    public String questionList(Model model, HttpSession session) {
        addCommonAttributes(session, model);
        return "question-list";
    }

    @GetMapping({"/questions/new", "/question-form", "/teacher/questions/new", "/teacher/question-form"})
    public String newQuestion(Model model, HttpSession session) {
        addCommonAttributes(session, model);
        return "question-form";
    }

    @GetMapping({"/questions/edit/{id}", "/teacher/questions/edit/{id}"})
    public String editQuestion(@PathVariable("id") Long id, Model model, HttpSession session) {
        addCommonAttributes(session, model);
        return "question-form";
    }
}