package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.ExamService;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class ExamController {

    @Autowired
    private ExamService examService;

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

        Map<String, Object> exam = examService.getExamMetadata(examId);
        int maxAttempts = 1;
        if (exam.get("max_attempts") != null) {
            int dbAttempts = ((Number) exam.get("max_attempts")).intValue();
            if (dbAttempts > 0) maxAttempts = dbAttempts;
        }

        List<Map<String, Object>> activeAttempts = examService.checkActiveAttempts(examId, enrollmentId);
        int attemptId;
        int currentAttemptNo;
        String sessionCacheKey = "exam_" + examId + "_user_" + enrollmentId + "_questions";
        String targetedQuestionSequence = (String) session.getAttribute(sessionCacheKey);

        if (!activeAttempts.isEmpty()) {
            int brokenAttemptId = ((Number) activeAttempts.get(0).get("attempt_id")).intValue();
            examService.terminateLiveSession(brokenAttemptId);
            session.removeAttribute(sessionCacheKey);
            
            int totalPastAttempts = examService.getPastAttemptsCount(examId, enrollmentId);
            model.addAttribute("examTitle", exam.get("title"));
            model.addAttribute("maxAttempts", maxAttempts);
            model.addAttribute("totalPastAttempts", totalPastAttempts);
            return "exam_lockout";
        }

        int totalPastAttempts = examService.getPastAttemptsCount(examId, enrollmentId);
        currentAttemptNo = totalPastAttempts + 1;

        if (currentAttemptNo > maxAttempts) {
            model.addAttribute("examTitle", exam.get("title"));
            model.addAttribute("maxAttempts", maxAttempts);
            model.addAttribute("totalPastAttempts", totalPastAttempts);
            return "exam_lockout";
        }

        if (targetedQuestionSequence == null || targetedQuestionSequence.isEmpty()) {
            targetedQuestionSequence = examService.resolveQuestionSequence(examId, true, null);
            session.setAttribute(sessionCacheKey, targetedQuestionSequence);
        }
        
        attemptId = examService.createNewAttemptSession(examId, enrollmentId, currentAttemptNo);
        List<ExamQuestionBean> questions = examService.buildShuffledQuestions(targetedQuestionSequence, exam);

        model.addAttribute("exam", exam);
        model.addAttribute("examId", examId);
        model.addAttribute("attemptId", attemptId);
        model.addAttribute("questions", questions);

        return "my_exam";
    }

    @PostMapping("/exam-submit")
    public String submitExam(HttpServletRequest request) {
        int attemptId = Integer.parseInt(request.getParameter("attemptId"));
        int examId = Integer.parseInt(request.getParameter("examId"));
        
        String fallIdStr = request.getParameter("questions.questionId") != null ? request.getParameter("questions.questionId") : "1";
        String answerText = request.getParameter("question_" + fallIdStr);
        if (answerText == null || answerText.trim().isEmpty()) {
            answerText = "No submission response recorded.";
        }

        examService.processSubmission(examId, attemptId, answerText);
        return "redirect:/student/exam-result/" + attemptId;
    }

    @GetMapping("/exam-result/{attemptId}")
    public String showExamResult(@PathVariable("attemptId") int attemptId, Model model, HttpSession session) {
        if (session.getAttribute("email") == null) return "redirect:/login";

        model.addAttribute("result", examService.getAttemptResultSummary(attemptId));
        return "exam_result";
    }
}
