package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Learning_Managnment_System.JWD_70_lms.Repository.ExamRepository;
import com.Learning_Managnment_System.JWD_70_lms.Repository.QuestionRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamQuestionBean;
import com.Learning_Managnment_System.JWD_70_lms.model.QuestionBean;

@Service
public class ExamService {

	private final ExamRepository examRepository;
	private final QuestionRepository questionRepository;

	public ExamService(ExamRepository examRepository, QuestionRepository questionRepository) {
		this.examRepository = examRepository;
		this.questionRepository = questionRepository;
	}

	public List<ExamBean> getAllExams() {
		return examRepository.findAll();
	}

	public ExamBean getExamById(Long examId) {
		return examRepository.findById(examId)
				.orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId));
	}

	public long createExam(ExamBean exam) {
		validateExam(exam);
		return examRepository.save(exam);
	}

	public int updateExam(ExamBean exam) {
		getExamById(exam.getExamId());
		validateExam(exam);
		int result = examRepository.update(exam);

		if (result == 0) {
			throw new IllegalArgumentException("Exam update failed");
		}
		return result;
	}

	@Transactional
	public int deleteExam(Long examId) {

		getExamById(examId);
		examRepository.removeAllQuestions(examId);
		int result = examRepository.deleteById(examId);
		if (result == 0) {
			throw new IllegalArgumentException("Exam delete failed");
		}
		return result;
	}

	public List<ExamQuestionBean> getSelectedQuestions(Long examId) {
		getExamById(examId);
		return examRepository.findQuestionsByExamId(examId);
	}

	public int addQuestion(ExamQuestionBean examQuestion) {
		if (examQuestion.getExamId() == null) {
			throw new IllegalArgumentException("Exam ID is required");
		}

		if (examQuestion.getQuestionId() == null) {
			throw new IllegalArgumentException("Question ID is required");
		}

		if (examQuestion.getMark() == null || examQuestion.getMark().signum() <= 0) {
			throw new IllegalArgumentException("Question mark must be greater than zero");
		}

		if (examQuestion.getSortOrder() == null || examQuestion.getSortOrder() <= 0) {
			throw new IllegalArgumentException("Sort order must be greater than zero");
		}

		getExamById(examQuestion.getExamId());
		return examRepository.addQuestionToExam(examQuestion);
	}

	public int removeQuestion(Long examId, Long questionId) {
		getExamById(examId);
		if (questionId == null) {
			throw new IllegalArgumentException("Question ID is required");
		}
		return examRepository.removeQuestionFromExam(examId, questionId);
	}

	public int removeAllQuestions(Long examId) {
		getExamById(examId);
		return examRepository.removeAllQuestions(examId);
	}

	private void validateExam(ExamBean exam) {
		if (exam.getCourseId() == null) {
			throw new IllegalArgumentException("Course is required");
		}
		if (exam.getBatchId() == null) {
			throw new IllegalArgumentException("Batch is required");
		}
		if (exam.getTitle() == null || exam.getTitle().isBlank()) {
			throw new IllegalArgumentException("Exam title is required");
		}
		if (exam.getExamType() == null || exam.getExamType().isBlank()) {
			throw new IllegalArgumentException("Exam type is required");
		}
		if (exam.getDurationMin() == null || exam.getDurationMin() <= 0) {
			throw new IllegalArgumentException("Duration must be greater than zero");
		}
		if (exam.getTotalMark() == null || exam.getTotalMark().signum() <= 0) {
			throw new IllegalArgumentException("Total mark must be greater than zero");
		}
		if (exam.getPassMark() == null || exam.getPassMark().signum() < 0) {
			throw new IllegalArgumentException("Pass mark cannot be negative");
		}
		if (exam.getPassMark().compareTo(exam.getTotalMark()) > 0) {
			throw new IllegalArgumentException("Pass mark cannot be greater than total mark");
		}
		if (exam.getStartAt() == null) {
			throw new IllegalArgumentException("Start date is required");
		}
		if (exam.getEndAt() == null) {
			throw new IllegalArgumentException("End date is required");
		}
		if (!exam.getEndAt().isAfter(exam.getStartAt())) {
			throw new IllegalArgumentException("End date must be after start date");
		}
		if (exam.getMaxAttempts() == null || exam.getMaxAttempts() <= 0) {
			throw new IllegalArgumentException("Maximum attempts must be greater than zero");
		}
		if (exam.getShuffleQuestions() == null) {
			exam.setShuffleQuestions(false);
		}
		if (exam.getStatus() == null || exam.getStatus().isBlank()) {
			throw new IllegalArgumentException("Exam status is required");
		}
	}

	public List<QuestionBean> getQuestionBank() {
		return questionRepository.findAll();
	}

	@Transactional
	public void saveSelectedQuestions(Long examId, List<Long> questionIds, List<String> marks) {

		getExamById(examId);
		if (questionIds == null || questionIds.isEmpty()) {
			throw new IllegalArgumentException("Please select at least one question.");
		}

		if (marks == null || questionIds.size() != marks.size()) {
			throw new IllegalArgumentException("Question and mark data are invalid.");
		}

		for (int i = 0; i < questionIds.size(); i++) {
			Long questionId = questionIds.get(i);
			if (questionId == null) {
				continue;
			}

			if (examRepository.existsQuestionInExam(examId, questionId)) {
				continue;
			}
			BigDecimal mark;

			try {
				mark = new BigDecimal(marks.get(i));
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid mark for question " + questionId);
			}

			if (mark.signum() <= 0) {
				throw new IllegalArgumentException("Question mark must be greater than zero.");
			}

			Integer nextOrder = examRepository.getNextSortOrder(examId);
			ExamQuestionBean examQuestion = new ExamQuestionBean();

			examQuestion.setExamId(examId);
			examQuestion.setQuestionId(questionId);
			examQuestion.setMark(mark);
			examQuestion.setSortOrder(nextOrder);
			examRepository.addQuestionToExam(examQuestion);
		}
	}
}