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

    // ==========================================
    // 1. Get All Questions
    // ==========================================
    public List<QuestionBean> getAllQuestions() {

        return questionRepository.getAllQuestions();
    }

    // ==========================================
    // 2. Get Question By ID
    // ==========================================
    public QuestionBean getQuestionById(Long questionId) {

        return questionRepository.getQuestionById(questionId);
    }

    // ==========================================
    // 3. Get Options By Question ID
    // ==========================================
    public List<QuestionOptionBean> getOptionsByQuestionId(Long questionId) {

        return questionRepository.getOptionsByQuestionId(questionId);
    }

    // ==========================================
    // 4. Save Question
    // ==========================================
    public Long saveQuestion(QuestionBean question) {

        return questionRepository.saveQuestion(question);
    }

    // ==========================================
    // 5. Save Option
    // ==========================================
    public int saveOption(QuestionOptionBean option) {

        return questionRepository.saveOption(option);
    }

    // ==========================================
    // 6. Update Question
    // ==========================================
    public int updateQuestion(QuestionBean question) {

        return questionRepository.updateQuestion(question);
    }
   
    // ==========================================
    // 7. Delete Question
    // ==========================================
    public int deleteQuestion(Long questionId) {

        return questionRepository.deleteQuestion(questionId);
    }

    // ==========================================
    // 8. Delete Options
    // ==========================================
    public int deleteOptionsByQuestionId(Long questionId) {

        return questionRepository.deleteOptionsByQuestionId(questionId);
    }
   
}