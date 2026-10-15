package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ExamRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public List<Map<String, Object>> getAvailableExams(String email) {
        return examRepository.findAvailableExams(email);
    }

    public List<Map<String, Object>> getExamResultsHistory(String email) {
        return examRepository.findHistoricResults(email);
    }

    public int resolveEnrollmentId(String email) {
        return examRepository.findEnrollmentIdByEmail(email);
    }

    public Map<String, Object> getExamMetadata(int examId) {
        return examRepository.findExamById(examId);
    }

    public List<Map<String, Object>> checkActiveAttempts(int examId, int enrollmentId) {
        return examRepository.findActiveProgressAttempt(examId, enrollmentId);
    }

    public int getPastAttemptsCount(int examId, int enrollmentId) {
        return examRepository.countTotalAttempts(examId, enrollmentId);
    }

    public void terminateLiveSession(int attemptId) {
        examRepository.forceTerminateAttempt(attemptId);
    }

    public int createNewAttemptSession(int examId, int enrollmentId, int attemptNo) {
        examRepository.insertNewAttempt(examId, enrollmentId, attemptNo);
        return examRepository.getLastInsertId();
    }

    public String resolveQuestionSequence(int examId, boolean isNewAttempt, String cachedSeq) {
        if (!isNewAttempt && cachedSeq != null && !cachedSeq.isEmpty()) {
            return cachedSeq;
        }
        List<Integer> poolQuestionIds = examRepository.findQuestionPoolIds(examId);
        if (!poolQuestionIds.isEmpty()) {
            Collections.shuffle(poolQuestionIds);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < poolQuestionIds.size(); i++) {
                sb.append(poolQuestionIds.get(i));
                if (i < poolQuestionIds.size() - 1) sb.append(",");
            }
            return sb.toString();
        }
        return "1";
    }

    public List<ExamQuestionBean> buildShuffledQuestions(String sequence, Map<String, Object> exam) {
        List<ExamQuestionBean> questions = new ArrayList<>();
        String[] orderedIds = sequence.split(",");
        for (String qIdStr : orderedIds) {
            if (qIdStr.trim().isEmpty()) continue;
            int targetedId = Integer.parseInt(qIdStr.trim());
            try {
                questions.add(examRepository.findQuestionMetadata(targetedId));
            } catch (Exception e) {
                ExamQuestionBean fallbackNode = new ExamQuestionBean();
                fallbackNode.setQuestionId(targetedId);
                fallbackNode.setQuestionText("Exam Core Module Question Analysis Node reference #" + targetedId);
                fallbackNode.setQuestionType("SHORT_ANSWER");
                fallbackNode.setQuestionMark(((Number) exam.get("total_mark")).doubleValue());
                questions.add(fallbackNode);
            }
        }
        return questions;
    }

    public void processSubmission(int examId, int attemptId, String answerText) {
        int operationalQuestionId = examRepository.findFirstValidQuestionId(examId);
        examRepository.saveOrUpdateAnswer(attemptId, operationalQuestionId, answerText);
        examRepository.finalizeAttemptSubmission(attemptId);
    }

    public Map<String, Object> getAttemptResultSummary(int attemptId) {
        return examRepository.findAttemptResultSummary(attemptId);
    }
}
