package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ExamRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 🟢 DYNAMIC ASSESSMENT ROUTER HOOK:
     * Maps the specific filter request to fetch ONLY the teacher's selected question collection 
     * out of the ExamRepository data access layer.
     */
    public List<ExamQuestionBean> getQuestionsForExam(int examId) {
        return examRepository.findQuestionsByExamId(examId);
    }

    @Transactional
    public void processSubmission(int examId, int attemptId, String answerText) {
        examRepository.saveSubmissionResponse(examId, attemptId, answerText);
    }

    public ExamAttemptBean getAttemptResultSummary(int attemptId) {
        return examRepository.findAttemptResultSummary(attemptId);
    }
}
