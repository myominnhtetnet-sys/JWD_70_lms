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
}
