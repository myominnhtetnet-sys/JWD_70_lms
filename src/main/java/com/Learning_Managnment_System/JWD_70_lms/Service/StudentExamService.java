package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentExamRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentExamService {

    private final StudentExamRepository examRepository;

    public StudentExamService(StudentExamRepository examRepository) {
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

    /**
     * Resolves the original question sequence linked to the exam.
     * Shuffles the IDs here in Java first so each attempt gets a unique, duplicate-free order.
     */
    public String resolveQuestionSequence(int examId) {
        List<Integer> ids = examRepository.findQuestionSequence(examId);
        
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        
        // 🟢 SHUFFLE AT SEQUENCING LAYER: Shuffling unique IDs directly avoids SQL execution crashes
        Collections.shuffle(ids);
        
        return ids.stream()
                  .map(String::valueOf)
                  .collect(Collectors.joining(","));
    }

    @Transactional
    public int createNewAttemptSession(int examId, int enrollmentId, int attemptNo) {
        return examRepository.insertNewAttemptSession(examId, enrollmentId, attemptNo);
    }

    /**
     * 🟢 FIXED BUILD METHOD:
     * Cleans, sanitizes, and deduplicates the sequence string before compiling the database call.
     * This guarantees that each exam question card is generated exactly once on your frontend sheet.
     */
    public List<ExamQuestionBean> buildShuffledQuestions(String sequence) {
        if (sequence == null || sequence.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 🟢 DEDUPLICATION FILTER LAYER: Strips out repeating IDs from the comma-separated string
        String cleanDeduplicatedSequence = Arrays.stream(sequence.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty() && id.matches("\\d+"))
                .distinct() // 👈 REMOVES DUPLICATE RECORD ARRAYS PERMANENTLY
                .collect(Collectors.joining(","));

        if (cleanDeduplicatedSequence.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch the perfectly filtered questions. SQL FIELD() ordering handles the shuffle naturally now!
        return examRepository.findQuestionsBySequence(cleanDeduplicatedSequence);
    }

    @Transactional
    public void processSubmission(int examId, int attemptId, String answerText) {
        examRepository.saveSubmissionResponse(examId, attemptId, answerText);
    }

    public ExamAttemptBean getAttemptResultSummary(int attemptId) {
        return examRepository.findAttemptResultSummary(attemptId);
    }
}
