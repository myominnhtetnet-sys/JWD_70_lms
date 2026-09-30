package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Learning_Managnment_System.JWD_70_lms.Service.QuestionService;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // ==========================================
    // 1. Get All Questions
    // ==========================================
    @GetMapping
    public List<QuestionBean> getAllQuestions() {

        return questionService.getAllQuestions();
    }

    // ==========================================
    // 2. Get Question By ID
    // ==========================================
    @GetMapping("/{id}")
    public QuestionBean getQuestionById(
            @PathVariable("id") Long questionId) {

        return questionService.getQuestionById(questionId);
    }

    // ==========================================
    // 3. Get Options By Question ID
    // ==========================================
    @GetMapping("/{id}/options")
    public List<QuestionOptionBean> getOptionsByQuestionId(
            @PathVariable("id") Long questionId) {

        return questionService.getOptionsByQuestionId(questionId);
    }

    // ==========================================
    // 4. Create Question
    // ==========================================
    @PostMapping
    public Long saveQuestion(
            @RequestBody QuestionBean question) {

        return questionService.saveQuestion(question);
    }

    // ==========================================
    // 5. Update Question
    // ==========================================
    @PutMapping("/{id}")
    public int updateQuestion(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionBean question) {

        question.setQuestionId(questionId);

        return questionService.updateQuestion(question);
    }

    // ==========================================
    // 6. Delete Question
    // ==========================================
    @DeleteMapping("/{id}")
    public int deleteQuestion(
            @PathVariable("id") Long questionId) {

        return questionService.deleteQuestion(questionId);
    }

    // ==========================================
    // 7. Create Option
    // ==========================================
    @PostMapping("/{id}/options")
    public int saveOption(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionOptionBean option) {

        option.setQuestionId(questionId);

        return questionService.saveOption(option);
    }
}