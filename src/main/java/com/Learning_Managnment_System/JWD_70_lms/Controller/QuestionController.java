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

    @GetMapping
    public List<QuestionBean> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public QuestionBean getQuestionById(
            @PathVariable("id") Long questionId) {
        return questionService.getQuestionById(questionId);
    }

    @GetMapping("/{id}/options")
    public List<QuestionOptionBean> getOptionsByQuestionId(
            @PathVariable("id") Long questionId) {
        return questionService.getOptionsByQuestionId(questionId);
    }

    @PostMapping
    public Long saveQuestion(@RequestBody QuestionBean question) {
        return questionService.saveQuestion(question);
    }

    @PutMapping("/{id}")
    public int updateQuestion(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionBean question) {

        question.setQuestionId(questionId);
        return questionService.updateQuestion(question);
    }

    @DeleteMapping("/{id}")
    public int deleteQuestion(
            @PathVariable("id") Long questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @PostMapping("/{id}/options")
    public int saveOption(
            @PathVariable("id") Long questionId,
            @RequestBody QuestionOptionBean option) {
        option.setQuestionId(questionId);
        return questionService.saveOption(option);
    }

    @DeleteMapping("/{id}/options")
    public int deleteOptions( @PathVariable("id") Long questionId) {
        return questionService.deleteOptionsByQuestionId(questionId);
    }
}

