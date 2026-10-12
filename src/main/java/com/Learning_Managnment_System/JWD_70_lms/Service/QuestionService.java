package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.QuestionRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionOptionBean;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionBean> getAllQuestions() {
        return questionRepository.getAllQuestions();
    }

    public QuestionBean getQuestionById(Long questionId) {
        return questionRepository.getQuestionById(questionId);
    }

    public List<QuestionOptionBean> getOptionsByQuestionId(Long questionId) {
        return questionRepository.getOptionsByQuestionId(questionId);
    }

    public Long saveQuestion(QuestionBean question) {
        return questionRepository.saveQuestion(question);
    }

    public int saveOption(QuestionOptionBean option) {
        return questionRepository.saveOption(option);
    }

    public int updateQuestion(QuestionBean question) {
        return questionRepository.updateQuestion(question);
    }

    public int deleteQuestion(Long questionId) {
        return questionRepository.deleteQuestion(questionId);
    }

    public int deleteOptionsByQuestionId(Long questionId) {
        return questionRepository.deleteOptionsByQuestionId(questionId);
    }
}
