
package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentExamRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;

@Service
public class StudentExamService {

    private final StudentExamRepository examRepository;

    public StudentExamService(StudentExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    // 1. Student Exam List
    public List<StudentExamBean> getAvailableExams(String email) {
        return examRepository.findAvailableExams(email);
    }

    // 2. Student Exam History
    public List<ExamAttemptBean> getExamResultsHistory(String email) {
        return examRepository.findExamResultsHistory(email);
    }

    // 3. Find Student Enrollment
    public int resolveEnrollmentId(String email) {
        Integer enrollmentId =
                examRepository.findEnrollmentIdByEmail(email);

        return enrollmentId == null ? 0 : enrollmentId;
    }

    // 4. Get Exam Metadata
    public StudentExamBean getExamMetadata(int examId) {
        return examRepository.findStudentExamMetadata(examId);
    }

    // 5. Check Active Attempts
    public List<ExamAttemptBean> checkActiveAttempts(
            int examId, int enrollmentId) {

        return examRepository.findActiveAttempts(examId, enrollmentId);
    }

    // 6. Terminate Active Attempt
    @Transactional
    public void terminateLiveSession(int attemptId) {
        examRepository.updateAttemptToTerminated(attemptId);
    }

    // 7. Count Previous Attempts
    public int getPastAttemptsCount(int examId, int enrollmentId) {
        Integer count =
                examRepository.countPastAttempts(examId, enrollmentId);

        return count == null ? 0 : count;
    }

    // 8. Resolve Question Sequence
    public String resolveQuestionSequence(int examId) {
        List<Integer> ids =
                examRepository.findQuestionSequence(examId);

        return ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    // 9. Create New Attempt
    @Transactional
    public int createNewAttemptSession(
            int examId, int enrollmentId, int attemptNo) {

        return examRepository.insertNewAttemptSession(
                examId, enrollmentId, attemptNo);
    }

    // 10. Build Exam Questions
    public List<ExamQuestionBean> buildShuffledQuestions(
            String sequence) {

        if (sequence == null || sequence.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<ExamQuestionBean> questions =
                examRepository.findQuestionsBySequence(sequence);

        Collections.shuffle(questions);

        return questions;
    }

    // 11. Submit Exam
    @Transactional
    public void processSubmission(
            int examId, int attemptId, String answerText) {

        examRepository.saveSubmissionResponse(
                examId, attemptId, answerText);
    }

    // 12. Get Exam Result
    public ExamAttemptBean getAttemptResultSummary(int attemptId) {
        return examRepository.findAttemptResultSummary(attemptId);
    }
}
