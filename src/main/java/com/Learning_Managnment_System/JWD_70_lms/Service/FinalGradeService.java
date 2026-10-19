
package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Learning_Managnment_System.JWD_70_lms.Repository.FinalGradeRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.FinalGradeBean;

@Service
public class FinalGradeService {

    private final FinalGradeRepository repository;

    public FinalGradeService(FinalGradeRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> getBatches() {
        return repository.getBatches();
    }

    public List<FinalGradeBean> calculateGrades(int batchId) {

        List<Map<String, Object>> rows =
                repository.getStudentsForGrades(batchId);

        List<FinalGradeBean> results = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            FinalGradeBean grade = new FinalGradeBean();

            grade.setEnrollmentId(
                    ((Number) row.get("enrollment_id")).intValue());

            grade.setUserId(
                    ((Number) row.get("user_id")).intValue());

            grade.setStudentName((String) row.get("student_name"));
            grade.setEmail((String) row.get("email"));

            BigDecimal assignmentPercent =
                    decimalValue(row.get("assignment_percent"));

            BigDecimal examPercent =
                    decimalValue(row.get("exam_percent"));

            BigDecimal attendancePercent =
                    decimalValue(row.get("attendance_percent"));

            assignmentPercent = clamp(assignmentPercent);
            examPercent = clamp(examPercent);
            attendancePercent = clamp(attendancePercent);

            // Assignment contributes 30%
            BigDecimal assignmentScore = assignmentPercent
                    .multiply(new BigDecimal("0.30"));

            // Exam contributes 60%
            BigDecimal examScore = examPercent
                    .multiply(new BigDecimal("0.60"));

            // Attendance contributes 10%
            BigDecimal attendanceScore = attendancePercent
                    .multiply(new BigDecimal("0.10"));

            BigDecimal totalScore = assignmentScore
                    .add(examScore)
                    .add(attendanceScore)
                    .setScale(2, RoundingMode.HALF_UP);

            grade.setAssignmentPercent(assignmentPercent);
            grade.setExamPercent(examPercent);
            grade.setAttendancePercent(attendancePercent);

            grade.setAssignmentScore(
                    assignmentScore.setScale(2, RoundingMode.HALF_UP));

            grade.setExamScore(
                    examScore.setScale(2, RoundingMode.HALF_UP));

            grade.setTotalScore(totalScore);
            grade.setGradeLetter(getGradeLetter(totalScore));
            grade.setResult(
                    totalScore.compareTo(new BigDecimal("60")) >= 0
                            ? "PASSED" : "FAILED");

            grade.setPublished(row.get("published_at") != null);

            results.add(grade);
        }

        return results;
    }

    private BigDecimal decimalValue(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.toString());
    }

    private BigDecimal clamp(BigDecimal score) {
        if (score.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (score.compareTo(new BigDecimal("100")) > 0) {
            return new BigDecimal("100");
        }
        return score;
    }

    private String getGradeLetter(BigDecimal score) {
        if (score.compareTo(new BigDecimal("90")) >= 0) return "A";
        if (score.compareTo(new BigDecimal("80")) >= 0) return "B";
        if (score.compareTo(new BigDecimal("70")) >= 0) return "C";
        if (score.compareTo(new BigDecimal("60")) >= 0) return "D";
        return "F";
    }


@Transactional
public void publishGrades(int batchId, int teacherId) {

    int missingCount =
            repository.countUngradedAssessments(batchId);

    if (missingCount > 0) {
        throw new IllegalStateException(
            "Publish မလုပ်နိုင်ပါ။ Assignment သို့မဟုတ် Exam grading "
            + "မပြီးသေးသော record " + missingCount + " ခု ရှိနေပါသည်။"
        );
    }

    List<FinalGradeBean> grades = calculateGrades(batchId);

    if (grades.isEmpty()) {
        throw new IllegalStateException(
                "ဒီ Batch မှာ Grade တွက်ဖို့ Student မရှိပါ။");
    }

    for (FinalGradeBean grade : grades) {
        repository.publishGrade(
                grade.getEnrollmentId(),
                grade.getAssignmentScore(),
                grade.getExamScore(),
                grade.getAttendancePercent(),
                grade.getTotalScore(),
                grade.getGradeLetter(),
                grade.getResult(),
                teacherId
        );
    }
}

}
