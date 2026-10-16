package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ExamRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public List<ExamBean> getAvailableExams(String email) {
        return examRepository.findAvailableExams(email);
    }

    public List<ExamAttemptBean> getExamResultsHistory(String email) {
        return examRepository.findExamResultsHistory(email);
    }

    public int resolveEnrollmentId(String email) {
        return examRepository.findEnrollmentIdByEmail(email);
    }

    public ExamBean getExamMetadata(int examId) {
        return examRepository.findExamMetadata(examId);
    }

    public List<ExamAttemptBean> checkActiveAttempts(int examId, int enrollmentId) {
        return examRepository.findActiveAttempts(examId, enrollmentId);
    }

    @Transactional
    public void terminateLiveSession(int attemptId) {
        examRepository.updateAttemptToTerminated(attemptId);
    }

    public int getPastAttemptsCount(int examId, int enrollmentId) {
        return examRepository.countPastAttempts(examId, enrollmentId);
    }

    public String resolveQuestionSequence(int examId) {
        List<Integer> ids = examRepository.findQuestionSequence(examId);
        return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Transactional
    public int createNewAttemptSession(int examId, int enrollmentId, int attemptNo) {
        return examRepository.insertNewAttemptSession(examId, enrollmentId, attemptNo);
    }

    public List<ExamQuestionBean> buildShuffledQuestions(String sequence) {
        if (sequence == null || sequence.trim().isEmpty()) return Collections.emptyList();
        List<ExamQuestionBean> questions = examRepository.findQuestionsBySequence(sequence);
        Collections.shuffle(questions);
        return questions;
    }

    @Transactional
    public void processSubmission(int examId, int attemptId, String answerText) {
        examRepository.saveSubmissionResponse(examId, attemptId, answerText);
    }

    public ExamAttemptBean getAttemptResultSummary(int attemptId) {
        return examRepository.findAttemptResultSummary(attemptId);
    }
}
