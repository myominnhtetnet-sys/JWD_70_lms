package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.ExamService;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionBean;

@Controller
@RequestMapping("/teacher/exams")
public class ExamController {

	private final ExamService examService;
	private final JdbcTemplate jdbcTemplate;

	public ExamController(ExamService examService,JdbcTemplate jdbcTemplate) {
		this.examService = examService;
		this.jdbcTemplate = jdbcTemplate;
	}

	@GetMapping
	public String examList(Model model) {
	    model.addAttribute("exams", examService.getAllExams());
	    model.addAttribute("courses", getAllCourses());
	    model.addAttribute("batches", getAllBatches());
	    return "exam-list";
	}

	private List<Map<String, Object>> getAllCourses() {
	    String sql = """
	        SELECT course_id, title
	        FROM courses
	        ORDER BY title
	    """;
	    return jdbcTemplate.queryForList(sql);
	}
	
	private List<Map<String, Object>> getAllBatches() {
	    String sql = """
	        SELECT batch_id, batch_code, title
	        FROM batches
	        ORDER BY batch_id DESC
	        """;
	    return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/create")
	public String createForm(Model model) {
	    model.addAttribute("exam", new ExamBean());
	    model.addAttribute("batches", getAllBatches());
	    model.addAttribute("courses", getAllCourses());
	    return "exam-form";
	}

	@GetMapping("/edit/{id}")
	public String editForm(
	        @PathVariable("id") Long examId, Model model,
	        RedirectAttributes redirectAttributes) {

	    try {
	        ExamBean exam = examService.getExamById(examId);
	        model.addAttribute("exam", exam);
	        model.addAttribute("batches", getAllBatches());
	        model.addAttribute("courses", getAllCourses());
	        return "exam-form";

	    } catch (IllegalArgumentException e) {
	       redirectAttributes.addFlashAttribute( "error", e.getMessage());
	        return "redirect:/teacher/exams";
	    }
	}

	@GetMapping("/view/{id}")
	public String viewExam( @PathVariable("id") Long examId, Model model,
	        RedirectAttributes redirectAttributes) {

	    try {
	        ExamBean exam = examService.getExamById(examId);
	        List<ExamQuestionBean> questions =examService.getSelectedQuestions(examId);

	        BigDecimal selectedTotalMark = questions.stream()
	                .map(ExamQuestionBean::getMark)
	                .filter(mark -> mark != null)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	        model.addAttribute("exam", exam);
	        model.addAttribute("questions", questions);
	        model.addAttribute("questionCount", questions.size());
	        model.addAttribute("selectedTotalMark", selectedTotalMark);

	        return "exam-detail";

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute( "error",e.getMessage());
	        return "redirect:/teacher/exams";
	    }
	}

	@PostMapping("/save")
	public String saveExam(ExamBean exam, RedirectAttributes redirectAttributes) {
		try {
			exam.setCreatedBy(1L);
			Long examId = examService.createExam(exam);
			redirectAttributes.addFlashAttribute("success", "Exam created successfully.");
			return "redirect:/teacher/exams/view/" + examId;
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/teacher/exams/create";
		}
	}


	@PostMapping("/update")
	public String updateExam(
	        ExamBean exam,
	        RedirectAttributes redirectAttributes) {

	    try {
	        ExamBean existingExam = examService.getExamById(exam.getExamId());

	        exam.setCourseId(existingExam.getCourseId());
	        exam.setBatchId(existingExam.getBatchId());
	        exam.setCreatedBy(existingExam.getCreatedBy());
	        examService.updateExam(exam);

	        redirectAttributes.addFlashAttribute(
	                "success","Exam updated successfully.");

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error",e.getMessage());
	    }

	    return "redirect:/teacher/exams";
	}
	@PostMapping("/delete/{id}")
	public String deleteExam(@PathVariable("id") Long examId, RedirectAttributes redirectAttributes) {
		try {
			examService.deleteExam(examId);
			redirectAttributes.addFlashAttribute("success", "Exam deleted successfully.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/teacher/exams";
	}

	@PostMapping("/{examId}/questions/remove")
	public String removeQuestion(@PathVariable("examId") Long examId, @RequestParam("questionId") Long questionId,
			RedirectAttributes redirectAttributes) {
		try {
			examService.removeQuestion(examId, questionId);
			redirectAttributes.addFlashAttribute("success", "Question removed from exam.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/teacher/exams/view/" + examId;
	}

	@GetMapping("/{examId}/questions")
	public String selectQuestions(@PathVariable("examId") Long examId, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			ExamBean exam = examService.getExamById(examId);
			List<QuestionBean> questions = examService.getQuestionBank();
			List<ExamQuestionBean> selectedQuestions = examService.getSelectedQuestions(examId);
			Set<Long> selectedQuestionIds = selectedQuestions.stream().map(ExamQuestionBean::getQuestionId)
					.collect(Collectors.toSet());

			model.addAttribute("exam", exam);
			model.addAttribute("questions", questions);
			model.addAttribute("selectedQuestions", selectedQuestions);
			model.addAttribute("selectedQuestionIds", selectedQuestionIds);
			return "exam-select-questions";

		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/teacher/exams";
		}
	}

	@PostMapping("/{examId}/questions/save")
	public String saveSelectedQuestions(@PathVariable("examId") Long examId,
			@RequestParam(value = "questionIds", required = false) List<Long> questionIds,
			@RequestParam(value = "marks", required = false) List<String> marks,
			RedirectAttributes redirectAttributes) {

		try {
			examService.saveSelectedQuestions(examId, questionIds, marks);
			redirectAttributes.addFlashAttribute("success", "Questions added to exam successfully.");
			return "redirect:/teacher/exams/view/" + examId;
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/teacher/exams/" + examId + "/questions";
		}
	}

	@PostMapping("/{examId}/questions/add")
	public String addQuestion(@PathVariable("examId") Long examId, @RequestParam("questionId") Long questionId,
			@RequestParam("mark") BigDecimal mark, @RequestParam("sortOrder") Integer sortOrder,
			RedirectAttributes redirectAttributes) {

		try {
			ExamQuestionBean examQuestion = new ExamQuestionBean();
			examQuestion.setExamId(examId);
			examQuestion.setQuestionId(questionId);
			examQuestion.setMark(mark);
			examQuestion.setSortOrder(sortOrder);
			examService.addQuestion(examQuestion);
			redirectAttributes.addFlashAttribute("success", "Question added to exam successfully.");

		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/teacher/exams/" + examId + "/questions";
	}
	
	@PostMapping("/update/prepare")
	public String prepareExamUpdate(
	        ExamBean exam, Model model,
	        RedirectAttributes redirectAttributes) {

	    try {
	        ExamBean existing = examService.getExamById(exam.getExamId());

	        exam.setCourseId(existing.getCourseId());
	        exam.setBatchId(existing.getBatchId());
	        exam.setCreatedBy(existing.getCreatedBy());

	        List<ExamQuestionBean> selectedQuestions =
	                examService.getSelectedQuestions(exam.getExamId());

	        Set<Long> selectedQuestionIds =
	                selectedQuestions.stream()
	                    .map(ExamQuestionBean::getQuestionId)
	                    .collect(Collectors.toSet());

	        model.addAttribute("exam", exam);
	        model.addAttribute("questions", examService.getQuestionBank());
	        model.addAttribute("selectedQuestions", selectedQuestions);
	        model.addAttribute("selectedQuestionIds", selectedQuestionIds);

	        return "exam-update-questions";

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error",e.getMessage());
	        return "redirect:/teacher/exams";
	    }
	}
	
	@PostMapping("/update/confirm")
	public String confirmExamUpdate( ExamBean exam,
	        @RequestParam(value = "questionIds",
	            required = false ) List<Long> questionIds,
	        RedirectAttributes redirectAttributes) {

	    try {
	        examService.updateExamWithQuestions(exam, questionIds);
	        redirectAttributes.addFlashAttribute( "success","Exam and questions updated successfully.");
	        return "redirect:/teacher/exams";

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error",e.getMessage() );
	        return "redirect:/teacher/exams";
	    }
	}
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

    /**
     * 🟢 WORKSPACE ENTRY CONTROLLER
     * ⚡ DECISIVE LOCKOUT FIX: Evaluates historical row counts defensively.
     * Prevents database transaction commit delays from throwing false-positive lockout flags.
     */
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
        String targetedQuestionSequence = (String) session.getAttribute(sessionCacheKey);

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

        // 🟢 THE FIX: Re-verify real-time attempt logs against list data rows to bypass transaction delay bounds
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

        if (targetedQuestionSequence == null || targetedQuestionSequence.isEmpty()) {
            targetedQuestionSequence = examService.resolveQuestionSequence(examId);
            session.setAttribute(sessionCacheKey, targetedQuestionSequence);
        }
        
        attemptId = examService.createNewAttemptSession(examId, enrollmentId, currentAttemptNo);
        List<ExamQuestionBean> questions = examService.buildShuffledQuestions(targetedQuestionSequence);

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
        
        // Evict local cache key signature strings cleanly on completion
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
