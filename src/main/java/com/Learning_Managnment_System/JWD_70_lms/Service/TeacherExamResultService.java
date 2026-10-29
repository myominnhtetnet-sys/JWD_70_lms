package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Learning_Managnment_System.JWD_70_lms.Repository.TeacherExamResultRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamGradingForm;
import com.Learning_Managnment_System.JWD_70_lms.model.ExamGradingForm.AnswerMark;

@Service
public class TeacherExamResultService {

    private final TeacherExamResultRepository repository;

    public TeacherExamResultService(TeacherExamResultRepository repository) {
        this.repository = repository;
    }

    public Long getTeacherId(String email) {
        return repository.findUserIdByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Logged-in user not found."));
    }

    public List<Map<String, Object>> getResults(Long teacherId) {
        return repository.findResults(teacherId);
    }

    public Map<String, Object> getAttempt(Long attemptId,Long teacherId) {

        Map<String, Object> attempt = repository
                .findAttempt(attemptId, teacherId, false)
                .orElseThrow(() ->
                        new IllegalArgumentException("Exam attempt not found or access denied."));

        checkStatus(attempt);
        return attempt;
    }

    public List<Map<String, Object>> getAnswers(Map<String, Object> attempt) {
        return repository.findAnswers(id(attempt.get("attemptId")), id(attempt.get("examId")));
    }

    public ExamGradingForm createForm( Map<String, Object> attempt,List<Map<String, Object>> answers) {

        ExamGradingForm form = new ExamGradingForm();
        form.setRemark((String) attempt.get("remark"));

        for (Map<String, Object> answer : answers) {
            if ("SHORT_ANSWER".equals(answer.get("questionType"))) {
                AnswerMark item = new AnswerMark();
                item.setQuestionId(id(answer.get("questionId")));
                item.setMark((BigDecimal) answer.get("markAwarded"));
                form.getAnswers().add(item);
            }           
        }
        
        return form;
    }

    @Transactional
    public void gradeAttempt( Long attemptId,Long teacherId,ExamGradingForm form) {

        Map<String, Object> attempt = repository
                .findAttempt(attemptId, teacherId, true)
                .orElseThrow(() ->
                        new IllegalArgumentException("Exam attempt not found or access denied."));

        checkStatus(attempt);
        List<Map<String, Object>> answers = getAnswers(attempt);

        if (answers.isEmpty()) {
            throw new IllegalArgumentException("This exam has no questions.");
        }

        BigDecimal totalMark = (BigDecimal) attempt.get("totalMark");
        BigDecimal passMark = (BigDecimal) attempt.get("passMark");

        if (totalMark == null || totalMark.signum() <= 0
                || passMark == null || passMark.signum() < 0
                || passMark.compareTo(totalMark) > 0) {

            throw new IllegalArgumentException("Exam total mark or pass mark is invalid.");
        }

        Map<Long, BigDecimal> submittedMarks = new HashMap<>();

        if (form.getAnswers() == null) {
            throw new IllegalArgumentException("Invalid grading form.");
        }

        for (AnswerMark item : form.getAnswers()) {

            if (item == null || item.getQuestionId() == null) {
                throw new IllegalArgumentException("Invalid question in grading form.");
            }

            if (submittedMarks.containsKey(item.getQuestionId())) {
                throw new IllegalArgumentException("Duplicate question in grading form.");
            }

            submittedMarks.put(item.getQuestionId(), item.getMark());
        }

        Set<Long> shortQuestionIds = new HashSet<>();
        BigDecimal questionTotal = BigDecimal.ZERO;

        for (Map<String, Object> answer : answers) {
            BigDecimal maxMark = (BigDecimal) answer.get("maxMark");

            if (maxMark == null || maxMark.signum() <= 0) {
                throw new IllegalArgumentException("Question maximum mark must be greater than 0.");
            }

            questionTotal = questionTotal.add(maxMark);
            String type = (String) answer.get("questionType");

            if (!Set.of("MCQ", "TRUE_FALSE", "SHORT_ANSWER").contains(type)) {
                throw new IllegalArgumentException("Unsupported question type.");
            }

            if ("SHORT_ANSWER".equals(type)) {
                Long questionId = id(answer.get("questionId"));
                shortQuestionIds.add(questionId);
                BigDecimal mark = submittedMarks.get(questionId);

                if (mark == null) {
                    throw new IllegalArgumentException("Please grade every short answer.");
                }

                if (mark.signum() < 0 || mark.compareTo(maxMark) > 0) {
                    throw new IllegalArgumentException( "Question " + questionId
                            + ": mark must be between 0 and "
                            + maxMark + ".");
                }

                if (mark.stripTrailingZeros().scale() > 2) {
                    throw new IllegalArgumentException( "Marks can have at most 2 decimal places.");
                }
            }
        }

        if (!submittedMarks.keySet().equals(shortQuestionIds)) {
            throw new IllegalArgumentException( "The grading form does not match this exam.");
        }

        if (questionTotal.signum() <= 0) {
            throw new IllegalArgumentException("Question marks total must be greater than 0.");
        }
        BigDecimal score = BigDecimal.ZERO;

        for (Map<String, Object> answer : answers) {

            Long questionId = id(answer.get("questionId"));
            String type = (String) answer.get("questionType");
            BigDecimal maxMark = (BigDecimal) answer.get("maxMark");

            BigDecimal awarded;
            Boolean correct;

            if ("SHORT_ANSWER".equals(type)) {
                awarded = submittedMarks.get(questionId);
                correct = null;

            } else {
                correct = isTrue(answer.get("optionCorrect"));
                awarded = correct ? maxMark : BigDecimal.ZERO;
            }

            repository.saveAnswerMark( attemptId, questionId, awarded, correct);
            score = score.add(awarded);
        }

     // မေးခွန်းအမှတ်ပေါင်းမှ Exam Total Mark အတိုင်း အချိုးကျပြောင်းမယ်
        score = score.multiply(totalMark).divide(questionTotal, 2, RoundingMode.HALF_UP);
        // ပြောင်းပြီးသားအမှတ်ကို Exam Pass Mark နဲ့နှိုင်းယှဉ်မယ်
        String result = score.compareTo(passMark) >= 0 ? "PASSED" : "FAILED";
        String remark = form.getRemark() == null ? "" : form.getRemark().trim();
        int updated = repository.completeGrading(attemptId, score, result, remark);

        if (updated != 1) {
            throw new IllegalArgumentException( "Unable to save exam grading.");
        }
    }

    private void checkStatus(Map<String, Object> attempt) {
        String status = (String) attempt.get("status");
        if (!"SUBMITTED".equals(status) && !"GRADED".equals(status)) {
            throw new IllegalArgumentException( "Only submitted or graded attempts can be reviewed.");
        }
    }

    private Long id(Object value) {
        return ((Number) value).longValue();
    }

    private boolean isTrue(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return value instanceof Number number && number.intValue() != 0;
    }
}

