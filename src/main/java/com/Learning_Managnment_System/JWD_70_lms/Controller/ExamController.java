package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.ExamService;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/exam-list")
    public String showExamList(Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";
        
        model.addAttribute("exams", examService.getAvailableExams(studentEmail));
        return "exam_list";
    }

    @GetMapping("/exam-results")
    public String showAllExamResults(Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";
        model.addAttribute("results", examService.getExamResultsHistory(studentEmail));
        return "exam_history";
    }

    @GetMapping("/exam-take/{id}")
    public String takeExam(@PathVariable("id") int examId, Model model, HttpSession session) {
        String studentEmail = (String) session.getAttribute("email");
        if (studentEmail == null) return "redirect:/login";

        int enrollmentId = examService.resolveEnrollmentId(studentEmail);
        if (enrollmentId == 0) return "redirect:/student/my-courses";

        ExamBean exam = examService.getExamMetadata(examId);
        List<ExamAttemptBean> activeAttempts = examService.checkActiveAttempts(examId, enrollmentId);
        
        int attemptId;
        String sessionCacheKey = "exam_" + examId + "_user_" + enrollmentId + "_questions";

        int maxAttemptsLimit = 1;
        if (exam.getMax_attempts() != null) {
            maxAttemptsLimit = exam.getMax_attempts().intValue();
        }

        // 1. Terminate or manage any stale ongoing raw unsubmitted states
        if (!activeAttempts.isEmpty()) {
            int brokenAttemptId = activeAttempts.get(0).getAttempt_id();
            examService.terminateLiveSession(brokenAttemptId);
            session.removeAttribute(sessionCacheKey);
            
            // Re-fetch clean status metrics directly after flushing dead sessions
            activeAttempts = examService.checkActiveAttempts(examId, enrollmentId);
        }

        // 2. Fetch the current completed records count
        int totalPastAttemptsCount = examService.getPastAttemptsCount(examId, enrollmentId);
        int currentAttemptNo = totalPastAttemptsCount + 1;

        // Re-verify real-time attempt logs against list data rows to bypass transaction delay bounds
        List<ExamAttemptBean> fullHistoryLog = examService.getExamResultsHistory(studentEmail);
        long matchedCurrentExamCount = fullHistoryLog.stream()
                .filter(attempt -> attempt.getExam_id() != null && attempt.getExam_id() == examId)
                .count();

        // If history logs conflict with the raw count, use the more reliable log index
        if (matchedCurrentExamCount >= currentAttemptNo) {
            currentAttemptNo = (int) matchedCurrentExamCount + 1;
        }

        // 3. Final validation bounds check can never drop false-positives
        if (currentAttemptNo > maxAttemptsLimit) {
            model.addAttribute("examTitle", exam.getTitle());
            model.addAttribute("maxAttempts", maxAttemptsLimit);
            model.addAttribute("totalPastAttempts", currentAttemptNo - 1);
            return "exam_lockout";
        }
        
        attemptId = examService.createNewAttemptSession(examId, enrollmentId, currentAttemptNo);

        // 🟢 FIXED: Calls the service method that fetches strictly the teacher's selected questions!
        List<ExamQuestionBean> questions = examService.getQuestionsForExam(examId);

        model.addAttribute("exam", exam);
        model.addAttribute("examId", examId);
        model.addAttribute("attemptId", attemptId);
        model.addAttribute("questions", questions); 
        return "my_exam";
    }

    @PostMapping("/exam-submit")
    public String submitExam(HttpServletRequest request, HttpSession session) {
        int attemptId = Integer.parseInt(request.getParameter("attemptId"));
        int examId = Integer.parseInt(request.getParameter("examId"));
        
        String fallIdStr = request.getParameter("questions.questionId") != null ? request.getParameter("questions.questionId") : "1";
        String answerText = request.getParameter("question_" + fallIdStr);
        if (answerText == null || answerText.trim().isEmpty()) {
            answerText = "No submission response recorded.";
        }

        examService.processSubmission(examId, attemptId, answerText);
        
        Integer enrollmentId = examService.resolveEnrollmentId((String) session.getAttribute("email"));
        String sessionCacheKey = "exam_" + examId + "_user_" + enrollmentId + "_questions";
        session.removeAttribute(sessionCacheKey);
        
        return "redirect:/student/exam-result/" + attemptId;
    }

    @GetMapping("/exam-result/{attemptId}")
    public String showExamResult(@PathVariable("attemptId") int attemptId, Model model, HttpSession session) {
        if (session.getAttribute("email") == null) return "redirect:/login";
        model.addAttribute("result", examService.getAttemptResultSummary(attemptId));
        return "exam_result";
    }
}
