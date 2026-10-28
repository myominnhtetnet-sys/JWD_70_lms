
package com.Learning_Managnment_System.JWD_70_lms.Controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Learning_Managnment_System.JWD_70_lms.Service.TeacherExamResultService;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamGradingForm;
import com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;

@Controller
@RequestMapping("/teacher/exam-results")
public class TeacherExamResultController {

	private final TeacherExamResultService service;

	// Local testing only
	private static final Long TEST_TEACHER_ID = 1L;

	public TeacherExamResultController(TeacherExamResultService service) {
		this.service = service;
	}

	private void addCommonAttributes(HttpSession session, Model model) {

		LoginBean currentUser = (LoginBean) session.getAttribute("currentUser");

		if (currentUser != null && currentUser.getFull_name() != null) {

			model.addAttribute("fullName", currentUser.getFull_name());

		} else if (session.getAttribute("fullName") != null) {

			model.addAttribute("fullName", session.getAttribute("fullName"));
		}
	}

	// 1. Exam Results List Page
	@GetMapping
	public String listResults(HttpSession session, Model model) {

		addCommonAttributes(session, model);
		loadList(model, TEST_TEACHER_ID);

		return "teacher-exam-result-list";
	}

	// 2. View Exam Result Page
	@GetMapping("/view/{attemptId}")
	public String viewResult(@PathVariable Long attemptId, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {

		addCommonAttributes(session, model);

		try {
			Map<String, Object> attempt = service.getAttempt(attemptId, TEST_TEACHER_ID);

			List<Map<String, Object>> answers = service.getAnswers(attempt);

			model.addAttribute("attempt", attempt);
			model.addAttribute("examAnswers", answers);

			return "teacher-exam-result-detail";

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("error", e.getMessage());

			return "redirect:/teacher/exam-results";
		}
	}

	// 3. Grade Exam Result Page
	@GetMapping("/grade/{attemptId}")
	public String gradeResult(@PathVariable Long attemptId, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {

		addCommonAttributes(session, model);

		try {
			Map<String, Object> attempt = service.getAttempt(attemptId, TEST_TEACHER_ID);

			List<Map<String, Object>> answers = service.getAnswers(attempt);

			model.addAttribute("attempt", attempt);
			model.addAttribute("examAnswers", answers);
			model.addAttribute("gradingForm", service.createForm(attempt, answers));

			return "exam-result-grade";

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("error", e.getMessage());

			return "redirect:/teacher/exam-results";
		}
	}

	// 4. Save Grades
	@PostMapping("/{attemptId}/grade")
	public String saveGrades(@PathVariable Long attemptId, @ModelAttribute("gradingForm") ExamGradingForm form,
			BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		addCommonAttributes(session, model);

		Map<String, Object> attempt;

		try {
			attempt = service.getAttempt(attemptId, TEST_TEACHER_ID);

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("error", e.getMessage());

			return "redirect:/teacher/exam-results";
		}

		List<Map<String, Object>> answers = service.getAnswers(attempt);

		try {
			if (bindingResult.hasErrors()) {
				throw new IllegalArgumentException("Please enter valid numbers for the marks.");
			}

			service.gradeAttempt(attemptId, TEST_TEACHER_ID, form);

			redirectAttributes.addFlashAttribute("success", "Exam graded successfully!");

			return "redirect:/teacher/exam-results";

		} catch (IllegalArgumentException e) {

			// Keep the entered form values if validation fails
			model.addAttribute("attempt", attempt);
			model.addAttribute("examAnswers", answers);
			model.addAttribute("gradingForm", form);
			model.addAttribute("error", e.getMessage());

			return "exam-result-grade";
		}
	}

	// Summary counts for the list page
	private void loadList(Model model, Long teacherId) {

		List<Map<String, Object>> results = service.getResults(teacherId);

		long graded = results.stream().filter(row -> "GRADED".equals(row.get("status"))).count();

		model.addAttribute("results", results);
		model.addAttribute("totalCount", results.size());
		model.addAttribute("gradedCount", graded);
		model.addAttribute("pendingCount", results.size() - graded);
	}
}
