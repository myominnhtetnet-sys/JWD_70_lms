
package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.Learning_Managnment_System.JWD_70_lms.Service.StudentExamService;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamAttemptBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;


@Controller
@RequestMapping("/student")
public class StudentExamController {

    private final StudentExamService examService;

    public StudentExamController(StudentExamService examService) {
        this.examService = examService;
    }

    // 1. Student Exam List
    @GetMapping("/exam-list")
    public String showExamList(
            Model model,
            HttpSession session) {

        String studentEmail =
                (String) session.getAttribute("email");

        if (studentEmail == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "exams",
                examService.getAvailableExams(studentEmail)
        );

        return "exam_list";
    }

    // 2. Student Exam History
    @GetMapping("/exam-results")
    public String showAllExamResults(
            Model model,
            HttpSession session) {

        String studentEmail =
                (String) session.getAttribute("email");

        if (studentEmail == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "results",
                examService.getExamResultsHistory(studentEmail)
        );

        return "exam_history";
    }

    // 3. Take Exam
    @GetMapping("/exam-take/{id}")
    public String takeExam(
            @PathVariable("id") int examId,
            Model model,
            HttpSession session) {

        String studentEmail =
                (String) session.getAttribute("email");

        if (studentEmail == null) {
            return "redirect:/login";
        }

        int enrollmentId =
                examService.resolveEnrollmentId(studentEmail);

        if (enrollmentId == 0) {
            return "redirect:/student/my-courses";
        }

        StudentExamBean exam =
                examService.getExamMetadata(examId);

        List<ExamAttemptBean> activeAttempts =
                examService.checkActiveAttempts(
                        examId, enrollmentId
                );

        String sessionCacheKey =
                "exam_" + examId
                + "_user_" + enrollmentId + "_questions";

        String questionSequence =
                (String) session.getAttribute(sessionCacheKey);

        int maxAttemptsLimit = 1;

        if (exam.getMax_attempts() != null) {
            maxAttemptsLimit =
                    exam.getMax_attempts().intValue();
        }

        /*
         * If an active attempt exists, do not automatically
         * terminate it. The current code below still needs
         * a service method to resume that attempt safely.
         */
        
        // teacher အပိုင်းနဲ့ student အပိုင်းပေါင်းပီး exam-result စစ်ရန် commend ပိတ်ထားခြင်း
//        if (!activeAttempts.isEmpty()) {
//            model.addAttribute("error",
//                    "You already have an active exam attempt.");
//            return "exam_lockout";
//        }
        
     // teacher အပိုင်းနဲ့ student အပိုင်းပေါင်းပီး exam-result စစ်ရန် အသစ်ထည့်လိုက်ခြင်း
        if (!activeAttempts.isEmpty()) {

            int pastAttempts =
                    examService.getPastAttemptsCount(
                            examId, enrollmentId
                    );

            model.addAttribute("examTitle", exam.getTitle());
            model.addAttribute("maxAttempts", maxAttemptsLimit);
            model.addAttribute("totalPastAttempts", pastAttempts);
            model.addAttribute("error",
                    "You already have an active exam attempt.");

            return "exam_lockout";
        }

        int pastAttempts =
                examService.getPastAttemptsCount(
                        examId, enrollmentId
                );

        int currentAttemptNo = pastAttempts + 1;

        if (currentAttemptNo > maxAttemptsLimit) {
            model.addAttribute("examTitle", exam.getTitle());
            model.addAttribute("maxAttempts", maxAttemptsLimit);
            model.addAttribute("totalPastAttempts", pastAttempts);

            return "exam_lockout";
        }

        if (questionSequence == null
                || questionSequence.isEmpty()) {

            questionSequence =
                    examService.resolveQuestionSequence(examId);

            session.setAttribute(
                    sessionCacheKey, questionSequence
            );
        }

        int attemptId =
                examService.createNewAttemptSession(
                        examId,
                        enrollmentId,
                        currentAttemptNo
                );

        List<ExamQuestionBean> questions =
                examService.buildShuffledQuestions(
                        questionSequence
                );

        model.addAttribute("exam", exam);
        model.addAttribute("examId", examId);
        model.addAttribute("attemptId", attemptId);
        model.addAttribute("questions", questions);

        return "my_exam";
    }

    // 4. Submit Exam
    @PostMapping("/exam-submit")
    public String submitExam(
            HttpServletRequest request,
            HttpSession session) {

        String studentEmail =
                (String) session.getAttribute("email");

        if (studentEmail == null) {
            return "redirect:/login";
        }

        String attemptIdValue =
                request.getParameter("attemptId");

        String examIdValue =
                request.getParameter("examId");

        if (attemptIdValue == null || examIdValue == null) {
            return "redirect:/student/exam-list";
        }

        int attemptId;
        int examId;

        try {
            attemptId = Integer.parseInt(attemptIdValue);
            examId = Integer.parseInt(examIdValue);
        } catch (NumberFormatException e) {
            return "redirect:/student/exam-list";
        }

        /*
         * This preserves the original single-answer submission
         * logic. Multi-question answer handling must be matched
         * to my_exam.html and ExamService.processSubmission().
         */
        String questionIdValue =
                request.getParameter("questions.questionId");

        if (questionIdValue == null) {
            questionIdValue = "1";
        }

        String answerText =
                request.getParameter("question_" + questionIdValue);

        if (answerText == null || answerText.trim().isEmpty()) {
            answerText = "No submission response recorded.";
        }

        examService.processSubmission(
                examId, attemptId, answerText
        );

        Integer enrollmentId =
                examService.resolveEnrollmentId(studentEmail);

        String sessionCacheKey =
                "exam_" + examId
                + "_user_" + enrollmentId + "_questions";

        session.removeAttribute(sessionCacheKey);

        return "redirect:/student/exam-result/" + attemptId;
    }

    // 5. View Exam Result
    @GetMapping("/exam-result/{attemptId}")
    public String showExamResult(
            @PathVariable("attemptId") int attemptId,
            Model model,
            HttpSession session) {

        String studentEmail =
                (String) session.getAttribute("email");

        if (studentEmail == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "result",
                examService.getAttemptResultSummary(attemptId)
        );

        return "exam_result";
    }
}
